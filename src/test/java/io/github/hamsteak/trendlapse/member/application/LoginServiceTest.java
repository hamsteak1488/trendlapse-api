package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.LoginCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    LoginService loginService;

    @Test
    void login_invokes_MemberRepository_find_and_return_memberId() {
        // given
        Username username = Username.of("Steve");
        Password password = Password.of("1234");
        Member member = new Member(1L, username, password, Email.of("abc@gmail.com"));
        when(memberRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(member));

        // when
        long memberId = loginService.login(new LoginCommand("Steve", "1234"));

        // then
        verify(memberRepository, only()).findByUsernameAndPassword(username, password);
        Assertions.assertThat(memberId).isEqualTo(1L);
    }
}