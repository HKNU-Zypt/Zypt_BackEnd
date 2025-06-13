package zypt.zyptapiserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.domain.dto.RefreshTokenRequestDto;
import zypt.zyptapiserver.domain.dto.SocialLoginDto;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO 자체 토큰도 매 앱 실행시 로그인 과정을 거쳐야함
    @PostMapping("/login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginDto socialLoginDto, HttpServletResponse response) {

        authService.handleAuthenticationFromSocialToken(response, socialLoginDto.type(), socialLoginDto.token());
        return ResponseEntity.ok("로그인 성공");
    }

    /**
     * 액세스 토큰 만료시 리프레시 토큰을 받아 검증 후 액세스토큰을 재발급해준다.
     * @param requestDto  리프레시 토큰 Dto
     * @param response    토큰 헤더에 발급용
     * @return            200 OK
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDto requestDto, HttpServletResponse response) {
        //TODO 테스트 해야함
        authService.authenticateUserFromToken(response, requestDto.refreshToken());
        return ResponseEntity.ok("로그인 성공");
    }

}

