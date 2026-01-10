package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.global.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginMemberArgumentResolverTest {
    MethodParameter loginMemberMethodParameter;
    MethodParameter objectMethodParameter;

    @Mock
    NativeWebRequest nativeWebRequest;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpSession httpSession;

    LoginMemberArgumentResolver loginMemberArgumentResolver = new LoginMemberArgumentResolver();

    private void loginMemberMethod(LoginMember loginMember, Object object) {
    }

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        Method loginMemberMethod = this.getClass().getDeclaredMethod("loginMemberMethod", LoginMember.class, Object.class);
        loginMemberMethodParameter = new MethodParameter(loginMemberMethod, 0);
        objectMethodParameter = new MethodParameter(loginMemberMethod, 1);
    }

    @Test
    void supportsParameter_returns_true_when_parameter_is_LoginMember() {
        // when
        boolean support = loginMemberArgumentResolver.supportsParameter(loginMemberMethodParameter);
        boolean notSupport = loginMemberArgumentResolver.supportsParameter(objectMethodParameter);

        // then
        assertThat(support).isTrue();
        assertThat(notSupport).isFalse();
    }

    @Test
    void resolveArgument_returns_session_LoginMember() {
        // given
        when(nativeWebRequest.getNativeRequest())
                .thenReturn(httpServletRequest);
        when(httpServletRequest.getSession(false))
                .thenReturn(httpSession);
        when(httpSession.getAttribute(SessionConst.LOGIN_MEMBER_ID))
                .thenReturn(1L);

        // when
        Object arg = loginMemberArgumentResolver.resolveArgument(loginMemberMethodParameter, null, nativeWebRequest, null);

        // then
        assertThat(arg)
                .isInstanceOfSatisfying(
                        LoginMember.class,
                        loginMember -> assertThat(loginMember.getMemberId()).isEqualTo(1L)
                );
    }

    @Test
    void resolveArgument_throws_UnauthorizedException_when_no_session() {
        // given
        when(nativeWebRequest.getNativeRequest())
                .thenReturn(httpServletRequest);
        when(httpServletRequest.getSession(false))
                .thenReturn(null);

        // when
        Throwable thrown = catchThrowable(() -> loginMemberArgumentResolver.resolveArgument(loginMemberMethodParameter, null, nativeWebRequest, null));

        // then
        assertThat(thrown).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void resolveArgument_throws_UnauthorizedException_when_no_attribute() {
        // given
        when(nativeWebRequest.getNativeRequest())
                .thenReturn(httpServletRequest);
        when(httpServletRequest.getSession(false))
                .thenReturn(httpSession);
        when(httpSession.getAttribute(SessionConst.LOGIN_MEMBER_ID))
                .thenReturn(null);

        // when
        Throwable thrown = catchThrowable(() -> loginMemberArgumentResolver.resolveArgument(loginMemberMethodParameter, null, nativeWebRequest, null));

        // then
        assertThat(thrown).isInstanceOf(UnauthorizedException.class);
    }
}