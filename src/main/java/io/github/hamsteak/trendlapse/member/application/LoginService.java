package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public long login(LoginCommand command) {
        Member member = memberRepository.findByUsernameAndPassword(Username.of(command.getUsername()), Password.of(command.getPassword()))
                .orElseThrow(() -> new MemberNotFoundException("Cannot find member."));

        return member.getId();
    }
}
