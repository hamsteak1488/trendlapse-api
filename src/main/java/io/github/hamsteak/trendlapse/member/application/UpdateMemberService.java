package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.UpdateMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberNotFoundException;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import io.github.hamsteak.trendlapse.member.domain.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMemberService {
    private final MemberRepository memberRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void update(long memberId, UpdateMemberCommand command) {
        passwordPolicy.validate(command.getPassword());
        String passwordHash = passwordEncoder.encode(command.getPassword());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Cannot find member."));

        if (command.getUsername() != null) {
            member.changeUsername(command.getUsername());
        }
        if (command.getPassword() != null) {
            member.changePassword(passwordHash);
        }
        if (command.getEmail() != null) {
            member.changeEmail(command.getEmail());
        }
    }
}
