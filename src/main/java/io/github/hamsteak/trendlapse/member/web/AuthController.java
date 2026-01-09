package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.global.session.SessionConst;
import io.github.hamsteak.trendlapse.member.application.LoginService;
import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.application.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(HttpSession httpSession, @RequestBody LoginRequest request) {
        long memberId = loginService.login(new LoginCommand(request.getUsername(), request.getPassword()));
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession httpSession) {
        httpSession.removeAttribute(SessionConst.LOGIN_MEMBER_ID);

        return ResponseEntity.ok().build();
    }
}
