package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    LoginService loginService;

    @Test
    void login_returns_memberId_when_credentials_match() {
        // given
        Username username = Username.of("Steve");
        Password password = Password.of("1234");
        Email email = Email.of("abc@gmail.com");
        Member member = new Member(1L, username, password, email);
        when(memberRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(member));

        // when
        long memberId = loginService.login(new LoginCommand("Steve", "1234"));

        // then
        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    void login_throws_MemberNotFoundException_when_credentials_invalid() {
        // given
        Username username = Username.of("Steve");
        Password password = Password.of("1234");
        when(memberRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> loginService.login(new LoginCommand("Steve", "1234")));

        // then
        assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
    }
}