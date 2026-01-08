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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @Mock
    Member member;

    @Test
    public void update_invokes_member_change() {
        // given
        long memberId = 1L;
        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        UpdateMemberCommand command = new UpdateMemberCommand("James", "5678", "def@gmail.com");
        UpdateMemberService updateMemberService = new UpdateMemberService(memberRepository);

        // when
        updateMemberService.update(memberId, command);

        // then
        verify(member, times(1)).changeUsername(command.getUsername());
        verify(member, times(1)).changePassword(command.getPassword());
        verify(member, times(1)).changeEmail(command.getEmail());
    }

    @Test
    public void update_throws_MemberNotFoundException_when_member_not_found() {
        // given
        when(memberRepository.findById(any()))
                .thenReturn(Optional.empty());

        UpdateMemberService updateMemberService = new UpdateMemberService(memberRepository);

        // when
        Throwable thrown = Assertions.catchThrowable(() -> updateMemberService.update(1L, null));

        // then
        Assertions.assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
    }
}
