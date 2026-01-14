package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.UpdateMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    PasswordPolicy passwordPolicy = new WeakPasswordPolicy();
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    Member member;

    @Test
    void update_invokes_member_change() {
        // given
        long memberId = 1L;
        UpdateMemberService updateMemberService = new UpdateMemberService(memberRepository, passwordPolicy, passwordEncoder);
        UpdateMemberCommand command = new UpdateMemberCommand("James", "5678", "def@gmail.com");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        updateMemberService.update(memberId, command);

        // then
        verify(member).changeUsername(command.getUsername());
        verify(member).changeEmail(command.getEmail());
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(member).changePassword(passwordCaptor.capture());
        String passwordHash = passwordCaptor.getValue();
        passwordEncoder.matches(command.getPassword(), passwordHash);
    }

    @Test
    void update_throws_MemberNotFoundException_when_member_not_found() {
        // given
        long memberId = 1L;
        UpdateMemberService updateMemberService = new UpdateMemberService(memberRepository, passwordPolicy, passwordEncoder);
        UpdateMemberCommand command = new UpdateMemberCommand("James", "5678", "def@gmail.com");
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        Throwable thrown = Assertions.catchThrowable(() -> updateMemberService.update(memberId, command));

        // then
        Assertions.assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
    }
}
