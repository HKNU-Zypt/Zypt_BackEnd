package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.dto.Token;
import zypt.zyptapiserver.service.member.MemberService;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.dto.RefreshTokenRequestDto;
import zypt.zyptapiserver.dto.member.SocialLoginDto;
import zypt.zyptapiserver.util.CookieUtils;

@Slf4j
@RestController
@Tag(name = "Auth API", description = "로그인, 인증, 인가에 대한 API")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/new")
    @Operation(summary = "로그인(회원가입)", description = "리프레시 토큰을 보낼 필요 X")
    public ResponseEntity<String> socialLogin(@RequestBody SocialLoginDto socialLoginDto, HttpServletResponse response) {
        UserInfo userInfo = authService.handleAuthenticationFromSocialToken(socialLoginDto.type(), socialLoginDto.token());
        Member member = authService.findOrCreateMemberBySocial(socialLoginDto.type(), userInfo);
        Token token = authService.generateTokenAndAuthenticated(member);

        // 응답에 토큰 삽입
        log.info("응답에 토큰 삽입");
        CookieUtils.addCookie(response, token.refreshToken());
        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.accessToken()); // 헤더에 액세스 토큰 삽입

        return ResponseEntity.ok("로그인 성공");
    }

    //TODO 로그아웃 과정
    // 1. 클라이언트에서 로그아웃 요청
    // 2. 서버에서는 리프레시 토큰을 삭제
    // 3. 성공 응답 반환
    // 4. 클라이언트는 액세스토큰을 삭제

    @DeleteMapping("/tokens")
    @Operation(summary = "로그아웃", description = "Authorization 헤더에 액세스토큰 필요, 서버 내 JWT 리프레시 토큰을 삭제함")
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
    @Operation(summary = "리프레시", description = "만료된 액세스토큰 재발급, 서버 JWT 리프레시 토큰을 Body에 넣어 전송")
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

    @DeleteMapping
    @Operation(summary = "회원탈퇴", description = "Authorization 헤더에 액세스토큰 필요, 구글의 경우 액세스 토큰 필요(현재는 재로그인 방법으로 획득할 것)")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal CustomUserDetails details) {
        authService.disconnect(details.getUsername());
        return ResponseEntity.ok("회원 탈퇴 완료");
    }
}

