package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.member.application.RegisterMemberService;
import io.github.hamsteak.trendlapse.member.application.UpdateMemberService;
import io.github.hamsteak.trendlapse.member.application.WithdrawMemberService;
import io.github.hamsteak.trendlapse.member.application.dto.RegisterMemberCommand;
import io.github.hamsteak.trendlapse.member.application.dto.UpdateMemberCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final RegisterMemberService registerMemberService;
    private final UpdateMemberService updateMemberService;
    private final WithdrawMemberService withdrawMemberService;

    @PostMapping
    public ResponseEntity<?> registerMember(@Validated @RequestBody RegisterMemberCommand command) {
        long memberId = registerMemberService.register(command);

        return ResponseEntity.created(URI.create("/members/" + memberId)).build();
    }

    @PatchMapping
    public ResponseEntity<?> updateMember(LoginMember loginMember, @Validated @RequestBody UpdateMemberCommand command) {
        updateMemberService.update(loginMember.getMemberId(), command);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> withdrawMember(LoginMember loginMember) {
        withdrawMemberService.withdraw(loginMember.getMemberId());

        return ResponseEntity.ok().build();
    }
}
