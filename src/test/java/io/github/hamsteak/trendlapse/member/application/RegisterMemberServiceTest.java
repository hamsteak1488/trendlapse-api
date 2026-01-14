package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.RegisterMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.DuplicateUsernameException;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    RegisterMemberService registerMemberService;

    @BeforeEach
    void setUp() {
        registerMemberService = new RegisterMemberService(memberRepository, encoder);
    }

    @Test
    void register_maps_command_to_member() {
        // given
        long memberId = 1L;
        when(memberRepository.saveAndFlush(any(Member.class)))
                .thenReturn(new Member(memberId, "Steve", "1234", "abc@gmail.com", encoder));
        RegisterMemberCommand command = new RegisterMemberCommand("Steve", "1234", "abc@gmail.com");

        // when
        registerMemberService.register(command);

        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).saveAndFlush(captor.capture());
        Member saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getUsername()).isEqualTo(command.getUsername());
        assertThat(encoder.matches(command.getPassword(), saved.getPasswordHash())).isTrue();
        assertThat(saved.getEmail()).isEqualTo(command.getEmail());
    }

    @Test
    void register_throws_DuplicateUsernameException_on_unique_violation() {
        // given
        when(memberRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);
        RegisterMemberCommand command = new RegisterMemberCommand("Steve", "1234", "abc@gmail.com");

        // when
        Throwable thrown = catchThrowable((() -> registerMemberService.register(command)));

        // then
        assertThat(thrown).isExactlyInstanceOf(DuplicateUsernameException.class);
    }
}
