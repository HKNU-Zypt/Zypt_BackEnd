package zypt.zyptapiserver.auth.filter;

import org.springframework.util.AntPathMatcher;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AuthService authService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final List<String> whiteList = Arrays.asList(
            "/api/auth/login/**",
            "/api/auth/refresh",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/favicon.ico"
    );

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private boolean isWhiteListed(String uri) {
        return whiteList.stream().anyMatch(pattern -> antPathMatcher.match(pattern, uri));
    }

    public JwtAuthenticationFilter(JwtUtils jwtUtils, AuthService authService) {
        this.jwtUtils = jwtUtils;
        this.authService = authService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 화이트 리스트의 경우 넘어감
        if (isWhiteListed(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveToken(request);

        // 검증 성공 시 Authentication 생성 및 인가
        if (accessToken != null && jwtUtils.validationToken(accessToken)) {
            String id = jwtUtils.extractId(accessToken);
            authService.registryAuthenticatedUser(id);
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    // jwt 토큰 추출 (access, refresh)
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
