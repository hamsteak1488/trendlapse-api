package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.global.session.SessionConst;
import io.github.hamsteak.trendlapse.member.application.LoginService;
import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.application.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    HttpSession httpSession;
    @Mock
    LoginService loginService;
    @InjectMocks
    AuthController authController;

    @Test
    void login_invokes_LoginService_login() {
        // when
        authController.login(httpSession, new LoginRequest("Steve", "1234"));

        // then
        verify(loginService, only()).login(new LoginCommand("Steve", "1234"));
    }

    @Test
    void login_creates_session() {
        // given
        long memberId = 1L;
        when(loginService.login(any())).thenReturn(memberId);

        // when
        authController.login(httpSession, new LoginRequest("Steve", "1234"));

        // then
        verify(httpSession, only()).setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);
    }

    @Test
    void logout_removes_session() {
        // when
        authController.logout(httpSession);

        // then
        verify(httpSession, only()).removeAttribute(SessionConst.LOGIN_MEMBER_ID);
    }
}