package zypt.zyptapiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> updateMemberNickName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("nickName") String nickName) {
        log.info("닉네임 업데이트");
        memberService.updateNickName(userDetails.getUsername(), nickName);


        return ResponseEntity.noContent().build();
    }
}
