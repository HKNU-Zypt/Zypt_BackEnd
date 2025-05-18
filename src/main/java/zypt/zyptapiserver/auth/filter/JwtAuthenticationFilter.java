package zypt.zyptapiserver.auth.filter;

import zypt.zyptapiserver.auth.exception.MissingTokenException;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AuthService authService;

    private static final String SOCIAL_TYPE_HEADER = "SocialType";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);

        if (accessToken == null) {
            log.warn("Missing access token");
            throw new MissingTokenException("AccessToken is required");
        }

        // 소셜 토큰일 경우
        if (isSocialAccessToken(request)) {
            authService.handleAuthenticationFromSocialToken(request, response, accessToken);

            // 자체 JWT 토큰일 경우
        } else {
            // 검증 성공 시 Authentication 생성 및 인가
            if (jwtUtils.validationToken(accessToken)) {
                String id = jwtUtils.extractId(accessToken);

                authService.registryAuthenticatedUser(id);

                // 액세스 토큰 만료시
            } else {
                authService.authenticateUserFromToken(response, accessToken);
            }
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }


    // 소셜 토큰 여부 체크
    private boolean isSocialAccessToken(HttpServletRequest request) {
        String socialTypeHeader = request.getHeader(SOCIAL_TYPE_HEADER);
        return socialTypeHeader != null && SocialType.checkSocialType(socialTypeHeader);
    }

    // jwt 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
