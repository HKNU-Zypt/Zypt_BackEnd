package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.domain.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.service.member.MemberService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.domain.dto.member.MemberInfoDto;

@Slf4j
@RestController
@Tag(name = "Member API", description = "멤버에 대한 API")
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    @Operation(summary = "회원 정보 조회", description = "액세스토큰 해더 필수")
    public ResponseEntity<MemberInfoDto> findMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberInfoDto member = memberService.findMember(userDetails.getUsername());
        return ResponseEntity.ok(member);
    }

    @GetMapping("/levelExp")
    @Operation(summary = "회원 정보(level, exp) 조회", description = "액세스토큰 해더 필수, 멤버 정보 + 레벨,exp 정보까지 조회")
    public ResponseEntity<MemberAndLevelInfoDto> findMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberAndLevelInfoDto memberInfo = memberService.findMemberInfo(userDetails.getUsername());
        return ResponseEntity.ok(memberInfo);
    }

    /**
     * 회원가입시 닉네임 변경
     * @param userDetails  현재 로그인한 사용자 id, nickname 정보
     * @param nickName     회원가입시 적용할 닉네임
     * @return             200 OK
     */
    @PostMapping("/signup")
    @Operation(summary = "첫 회원가입 시 닉네임 설정", description = "액세스토큰 해더 필수")
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
    @Operation(summary = "닉네임 변경", description = "액세스토큰 해더 필수 \n\n 해당 API는 회원가입 이후에 마이페이지에서 닉네임 변경을 원할시 사용")
    public ResponseEntity<String> updateMemberNickName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("nickName") String nickName) {
        memberService.updateNickName(userDetails.getUsername(), nickName);
        return ResponseEntity.ok("닉네임 변경 완료");
    }


}
