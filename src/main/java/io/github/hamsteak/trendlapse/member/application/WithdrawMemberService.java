package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberNotFoundException;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawMemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void withdraw(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Cannot find member."));
        memberRepository.delete(member);
    }
}
