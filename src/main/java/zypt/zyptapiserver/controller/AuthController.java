package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.domain.dto.RefreshTokenRequestDto;
import zypt.zyptapiserver.domain.dto.SocialLoginDto;
import zypt.zyptapiserver.domain.enums.SocialType;

@Slf4j
@RestController
@Tag(name = "Auth API", description = "로그인, 인증, 인가에 대한 API")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> socialLogin(@RequestBody SocialLoginDto socialLoginDto, HttpServletResponse response) {
        authService.handleAuthenticationFromSocialToken(response, socialLoginDto.type(), socialLoginDto.token());

        if (socialLoginDto.type() == SocialType.NAVER) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();

            memberService.saveSocialRefreshToken(memberId, socialLoginDto.refreshToken(), socialLoginDto.type());

        }

        return ResponseEntity.ok("로그인 성공");
    }

    //TODO 로그아웃 과정
    // 1. 클라이언트에서 로그아웃 요청
    // 2. 서버에서는 리프레시 토큰을 삭제
    // 3. 성공 응답 반환
    // 4. 클라이언트는 액세스토큰을 삭제
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails details) {
        authService.logout(details.getUsername());
        return ResponseEntity.ok("로그아웃 성공");
    }

    /**
     * 액세스 토큰 만료시 리프레시 토큰을 받아 검증 후 액세스토큰을 재발급해준다.
     * @param requestDto  리프레시 토큰 Dto
     * @param response    토큰 헤더에 발급용
     * @return            200 OK
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@Valid @RequestBody RefreshTokenRequestDto requestDto, HttpServletResponse response) {
        authService.authenticateUserFromToken(response, requestDto.refreshToken());
        return ResponseEntity.ok("로그인 성공");
    }

    /**
     * 회원 탈퇴
     * 1. 네이버 구글의 경우 서버내에서 소셜 리프레시 토큰 조회
     * 1.1 구글의 경우 재로그인으로 액세스토큰 취득
     * 2. 토큰과 함께 연동 해제 요청 전송
     * 3. 소셜 리프레시 토큰 삭제
     * @return
     */

    //TODO String 말고 객체로 바꿀 것
    @DeleteMapping("")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal CustomUserDetails details, @RequestBody(required = false) RefreshTokenRequestDto requestDto) {
        authService.disconnect(details.getUsername(), requestDto.refreshToken());
        return ResponseEntity.ok("회원 탈퇴 완료");
    }
}

