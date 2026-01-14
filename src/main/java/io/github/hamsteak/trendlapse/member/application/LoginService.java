package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.domain.LoginFailedException;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public long login(LoginCommand command) {
        Member member = memberRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new LoginFailedException("Cannot find member."));

        if (!passwordEncoder.matches(command.getPassword(), member.getPasswordHash())) {
            throw new LoginFailedException("Login Failed.");
        }

        return member.getId();
    }
}
