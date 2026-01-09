package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawMemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @Test
    void withdraw_invokes_MemberRepository_deleteById() {
        // given
        WithdrawMemberService withdrawMemberService = new WithdrawMemberService(memberRepository);
        final long memberId = 1L;
        Member member = new Member(memberId, Username.of("Steve"), Password.of("1234"), Email.of("abc@gmail.com"));
        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        // when
        withdrawMemberService.withdraw(memberId);

        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        Mockito.verify(memberRepository, times(1)).delete(captor.capture());

        Member argMember = captor.getValue();
        Assertions.assertThat(argMember).isEqualTo(member);
    }

    @Test
    public void withdraw_throws_MemberNotFoundException_when_member_not_found() {
        // given
        when(memberRepository.findById(any())).thenReturn(Optional.empty());
        WithdrawMemberService withdrawMemberService = new WithdrawMemberService(memberRepository);

        // when
        Throwable thrown = Assertions.catchThrowable(() -> withdrawMemberService.withdraw(1L));

        // then
        Assertions.assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
    }
}
