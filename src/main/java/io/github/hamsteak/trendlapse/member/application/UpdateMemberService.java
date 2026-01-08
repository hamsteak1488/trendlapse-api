package io.github.hamsteak.trendlapse.member.application;

import io.github.hamsteak.trendlapse.member.application.dto.UpdateMemberCommand;
import io.github.hamsteak.trendlapse.member.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void update(long memberId, UpdateMemberCommand command) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Cannot find member."));

        if (command.getUsername() != null) {
            member.changeUsername(Username.of(command.getUsername()));
        }
        if (command.getPassword() != null) {
            member.changePassword(Password.of(command.getPassword()));
        }
        if (command.getEmail() != null) {
            member.changeEmail(Email.of(command.getEmail()));
        }
    }
}
