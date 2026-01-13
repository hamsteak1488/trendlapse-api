package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.global.session.SessionConst;
import io.github.hamsteak.trendlapse.member.application.LoginService;
import io.github.hamsteak.trendlapse.member.application.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    HttpSession httpSession;
    @Mock
    LoginService loginService;
    @InjectMocks
    AuthController authController;

    @Test
    void login_calls_LoginService_with_request_values() {
        // when
        authController.login(httpSession, new LoginRequest("Steve", "1234"));

        // then
        verify(loginService).login(argThat(command ->
                command.getUsername().equals("Steve") && command.getPassword().equals("1234"))
        );
    }

    @Test
    void login_store_member_id_in_session() {
        // given
        long memberId = 1L;
        when(loginService.login(any())).thenReturn(memberId);

        // when
        authController.login(httpSession, new LoginRequest("Steve", "1234"));

        // then
        verify(httpSession).setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);
    }

    @Test
    void logout_removes_member_id_from_session() {
        // when
        authController.logout(httpSession);

        // then
        verify(httpSession).invalidate();
    }
}