package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.MemberView;
import io.github.hamsteak.trendlapse.member.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetMemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    GetMemberService getMemberService;

    @Test
    void get_returns_MemberView() {
        // given
        long memberId = 1L;
        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(new Member(memberId, Username.of("Steve"), Password.of("1234"), Email.of("abc@gmail.com"))));

        // when
        MemberView memberView = getMemberService.get(memberId);

        // then
        assertThat(memberView.getUsername()).isEqualTo("Steve");
        assertThat(memberView.getEmail()).isEqualTo("abc@gmail.com");

    }
}
