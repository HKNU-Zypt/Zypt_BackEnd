package zypt.zyptapiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.domain.dto.MemberInfoDto;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public ResponseEntity<MemberInfoDto> findMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberInfoDto infoDto = memberService.findMember(userDetails.getUsername());
        return ResponseEntity.ok(infoDto);
    }


    @PostMapping("/signup")
    public ResponseEntity<Void> updateMemberNickName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("nickName") String nickName) {
        log.info("닉네임 업데이트");

        memberService.updateNickName(userDetails.getUsername(), nickName);
        return ResponseEntity.noContent().build();
    }
}
