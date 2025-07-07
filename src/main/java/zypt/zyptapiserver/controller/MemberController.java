package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member API", description = "멤버에 대한 API")
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<MemberInfoDto> findMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberInfoDto infoDto = memberService.findMember(userDetails.getUsername());
        return ResponseEntity.ok(infoDto);
    }

    /**
     * 회원가입시 닉네임 변경
     * @param userDetails  현재 로그인한 사용자 id, nickname 정보
     * @param nickName     회원가입시 적용할 닉네임
     * @return             200 OK
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> updateMemberNickNameForSignUp(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("nickName") String nickName) {
        log.info("닉네임 업데이트");

        memberService.updateNickName(userDetails.getUsername(), nickName);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상시 닉네임 변경
     * @param userDetails 현재 로그인한 사용자 id, nickname 정보
     * @param nickName    변경할 닉네임
     * @return             200 OK
     */
    @PatchMapping("")
    public ResponseEntity<String> updateMemberNickName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("nickName") String nickName) {
        memberService.updateNickName(userDetails.getUsername(), nickName);
        return ResponseEntity.ok("닉네임 변경 완료");
    }


}
