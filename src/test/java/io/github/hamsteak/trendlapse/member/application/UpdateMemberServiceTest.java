package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.UpdateMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberNotFoundException;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @Mock
    Member member;

    @Test
    void update_invokes_member_change() {
        // given
        long memberId = 1L;
        UpdateMemberService updateMemberService = new UpdateMemberService(memberRepository);
        UpdateMemberCommand command = new UpdateMemberCommand("James", "5678", "def@gmail.com");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        updateMemberService.update(memberId, command);

        // then
        verify(member).changeUsername(command.getUsername());
        verify(member).changePassword(command.getPassword());
        verify(member).changeEmail(command.getEmail());
    }

    @Test
    void update_throws_MemberNotFoundException_when_member_not_found() {
        // given
        long memberId = 1L;
        UpdateMemberService updateMemberService = new UpdateMemberService(memberRepository);
        UpdateMemberCommand command = new UpdateMemberCommand("James", "5678", "def@gmail.com");
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        Throwable thrown = Assertions.catchThrowable(() -> updateMemberService.update(memberId, command));

        // then
        Assertions.assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
    }
}
