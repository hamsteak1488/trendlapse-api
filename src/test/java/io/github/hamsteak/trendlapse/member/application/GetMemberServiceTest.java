package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.MemberView;
import io.github.hamsteak.trendlapse.member.domain.Member;
import io.github.hamsteak.trendlapse.member.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    @InjectMocks
    GetMemberService getMemberService;

    @Test
    void get_returns_MemberView() {
        // given
        long memberId = 1L;
        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(new Member(memberId, "Steve", "1234", "abc@gmail.com", encoder)));

        // when
        MemberView memberView = getMemberService.get(memberId);

        // then
        assertThat(memberView.getUsername()).isEqualTo("Steve");
        assertThat(memberView.getEmail()).isEqualTo("abc@gmail.com");

    }
}
