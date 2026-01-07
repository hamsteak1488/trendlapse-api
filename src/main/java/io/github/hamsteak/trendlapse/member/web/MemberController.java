package io.github.hamsteak.trendlapse.member.web;

import io.github.hamsteak.trendlapse.member.application.RegisterMemberService;
import io.github.hamsteak.trendlapse.member.application.dto.RegisterMemberCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final RegisterMemberService registerMemberService;

    @PostMapping
    public ResponseEntity<?> registerMember(@Validated @RequestBody RegisterMemberCommand command) {
        long memberId = registerMemberService.register(command);

        return ResponseEntity.created(URI.create("/members/" + memberId)).build();
    }
}
