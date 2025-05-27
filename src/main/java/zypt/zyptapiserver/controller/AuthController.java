package zypt.zyptapiserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.domain.dto.SocialLoginDto;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SocialLoginDto socialLoginDto, HttpServletResponse response) {
        authService.handleAuthenticationFromSocialToken(response, socialLoginDto.type(), socialLoginDto.token());

        return ResponseEntity.ok("로그인 성공");
    }
}

