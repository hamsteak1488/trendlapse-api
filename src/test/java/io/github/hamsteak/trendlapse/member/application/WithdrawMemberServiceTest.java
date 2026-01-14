package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberNotFoundException;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import io.github.hamsteak.trendlapse.member.domain.Password;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawMemberServiceTest {
    @Mock
    Password password;
    @Mock
    MemberRepository memberRepository;

    @Test
    void withdraw_deletes_found_member() {
        // given
        long memberId = 1L;
        WithdrawMemberService withdrawMemberService = new WithdrawMemberService(memberRepository);
        Member member = new Member(memberId, "Steve", "1234", "abc@gmail.com");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        withdrawMemberService.withdraw(memberId);

        // then
        verify(memberRepository).delete(member);
    }

    @Test
    void withdraw_throws_MemberNotFoundException_when_member_not_found() {
        // given
        long memberId = 1L;
        WithdrawMemberService withdrawMemberService = new WithdrawMemberService(memberRepository);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        Throwable thrown = Assertions.catchThrowable(() -> withdrawMemberService.withdraw(memberId));

        // then
        assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
    }
}
