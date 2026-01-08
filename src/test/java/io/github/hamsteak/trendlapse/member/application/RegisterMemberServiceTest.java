package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.RegisterMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    RegisterMemberService registerMemberService;

    @Test
    void register_maps_command_to_member() {
        // given
        when(memberRepository.saveAndFlush(any(Member.class)))
                .thenReturn(new Member(1L, new Username("Steve"), new Password("1234"), new Email("abc@gmail.com")));
        RegisterMemberCommand command = new RegisterMemberCommand("Steve", "1234", "abc@gmail.com");

        // when
        registerMemberService.register(command);

        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        Mockito.verify(memberRepository).saveAndFlush(captor.capture());

        Member saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getUsername()).isEqualTo(Username.of(command.getUsername()));
        assertThat(saved.getPassword()).isEqualTo(Password.of(command.getPassword()));
        assertThat(saved.getEmail()).isEqualTo(Email.of(command.getEmail()));
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
