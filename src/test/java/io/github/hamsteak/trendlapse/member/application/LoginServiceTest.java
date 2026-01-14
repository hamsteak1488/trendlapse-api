package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.domain.LoginFailedException;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    MemberRepository memberRepository;
    LoginService loginService;
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        loginService = new LoginService(memberRepository, encoder);
    }

    @Test
    void login_returns_memberId_when_credentials_match() {
        // given
        Member member = new Member(1L, "Steve", "1234", "abc@gmail.com", encoder);
        when(memberRepository.findByUsername("Steve"))
                .thenReturn(Optional.of(member));

        // when
        long memberId = loginService.login(new LoginCommand("Steve", "1234"));

        // then
        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    void login_throws_LoginFailedException_when_member_not_found() {
        // given
        when(memberRepository.findByUsername("James"))
                .thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> loginService.login(new LoginCommand("James", "1234")));

        // then
        assertThat(thrown).isInstanceOf(LoginFailedException.class);
    }
}