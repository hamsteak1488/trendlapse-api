package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.RegisterMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.DuplicateUsernameException;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterMemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public long register(RegisterMemberCommand command) {
        try {
            Member entity = new Member(null, command.getUsername(), command.getPassword(), command.getEmail());
            Member member = memberRepository.saveAndFlush(entity);
            return member.getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateUsernameException("Username cannot be duplicated.");
        }
    }
}
