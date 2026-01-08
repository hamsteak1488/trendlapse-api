package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.RegisterMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
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
            Member member = new Member(
                    null,
                    Username.of(command.getUsername()),
                    Password.of(command.getPassword()),
                    Email.of(command.getEmail())
            );
            member = memberRepository.saveAndFlush(member);
            return member.getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateUsernameException("Username cannot be duplicated.");
        }
    }
}
