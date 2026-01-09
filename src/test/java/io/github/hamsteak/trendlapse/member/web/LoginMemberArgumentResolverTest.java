package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.global.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginMemberArgumentResolverTest {
    @Mock
    MethodParameter methodParameter;
    @Mock
    ModelAndViewContainer modelAndViewContainer;
    @Mock
    NativeWebRequest nativeWebRequest;
    @Mock
    WebDataBinderFactory webDataBinderFactory;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpSession httpSession;

    LoginMemberArgumentResolver loginMemberArgumentResolver = new LoginMemberArgumentResolver();

    @Test
    void supportsParameter_returns_true_when_parameter_is_LoginMember() throws NoSuchMethodException {
        // given
        Method loginMemberMethod = this.getClass().getDeclaredMethod("loginMemberMethod", LoginMember.class, Object.class);
        Parameter loginMemberParameter = loginMemberMethod.getParameters()[0];
        Parameter objectParameter = loginMemberMethod.getParameters()[1];

        // when
        boolean support = loginMemberArgumentResolver.supportsParameter(MethodParameter.forParameter(loginMemberParameter));
        boolean notSupport = loginMemberArgumentResolver.supportsParameter(MethodParameter.forParameter(objectParameter));

        // then
        assertThat(support).isTrue();
        assertThat(notSupport).isFalse();
    }

    private void loginMemberMethod(LoginMember loginMember, Object object) {
    }

    @Test
    void resolveArgument_returns_session_memberId() {
        // given
        when(nativeWebRequest.getNativeRequest())
                .thenReturn(httpServletRequest);
        when(httpServletRequest.getSession(false))
                .thenReturn(httpSession);
        when(httpSession.getAttribute(SessionConst.LOGIN_MEMBER_ID))
                .thenReturn(1L);

        // when
        Object arg = loginMemberArgumentResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);

        // then
        assertThat(arg).isNotNull();
        assertThat(arg).isInstanceOf(LoginMember.class);
        assertThat(((LoginMember) arg).getMemberId()).isEqualTo(1L);
    }

    @Test
    void resolveArgument_throws_UnauthorizedException_when_no_session() {
        // given
        when(nativeWebRequest.getNativeRequest())
                .thenReturn(httpServletRequest);
        when(httpServletRequest.getSession(false))
                .thenReturn(null);

        // when
        Throwable thrown = catchThrowable(() -> loginMemberArgumentResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory));

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
        Throwable thrown = catchThrowable(() -> loginMemberArgumentResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory));

        // then
        assertThat(thrown).isInstanceOf(UnauthorizedException.class);
    }
}