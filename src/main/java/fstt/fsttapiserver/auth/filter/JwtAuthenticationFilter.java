package fstt.fsttapiserver.auth.filter;

import fstt.fsttapiserver.auth.exception.InvalidTokenException;
import fstt.fsttapiserver.auth.exception.MissingTokenException;
import fstt.fsttapiserver.auth.service.KakaoService;
import fstt.fsttapiserver.auth.user.CustomUserDetails;
import fstt.fsttapiserver.auth.user.KakaoUserInfo;
import fstt.fsttapiserver.domain.Member;
import fstt.fsttapiserver.repository.MemberRepository;
import fstt.fsttapiserver.repository.RedisRepository;
import fstt.fsttapiserver.util.CookieUtils;
import fstt.fsttapiserver.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private final RedisRepository tokenRepository;

    private static final String SOCIAL_TYPE_HEADER = "SocialType";
    private static final String SOCIAL_TYPE_KAKAO = "kakao";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);

        if (accessToken == null) {
            log.error("Missing access token");
            throw new MissingTokenException("AccessToken is required");
        }

        // 소셜 토큰일 경우
        if (isSocialAccessToken(request)) {
            KakaoUserInfo kakaoUserInfo = KakaoService.getKakaoUserInfo(accessToken);

            if (kakaoUserInfo == null) {
                throw new InvalidTokenException("social AccessToken is invalid or malformed");
            }


            // 회원가입 하지 않았다면 가입
            Member member = memberRepository.findBySocialId(kakaoUserInfo.getId())
                    .orElse(null);

            if (member == null) {
                member = Member.builder()
                        .id(String.valueOf(UUID.randomUUID()))
                        .name(kakaoUserInfo.getNickname())
                        .socialId(kakaoUserInfo.getId())
                        .build();

                // 회원 db에 저장 및 토큰 생성
                memberRepository.save(member);
            }

            String newAccessToken = jwtUtils.generateAccessToken(member.getId());
            String newRefreshToken = jwtUtils.generateRefreshToken(member.getId());

            tokenRepository.saveRefreshToken(member.getId(), newRefreshToken); // redis에 리프레시 토큰 저장

            authenticateUser(member.getId());

            // 응답에 토큰 삽입
            CookieUtils.addCookie(response, newRefreshToken);
            response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);

            // 자체 JWT 토큰일 경우
        } else {
            // 검증 성공 시 Authentication 생성 및 인가
            if (jwtUtils.validationToken(accessToken)) {
                String memberId = jwtUtils.extractUserId(accessToken);
                authenticateUser(memberId);

                // 액세스 토큰 만료 시 claims를 반환
            } else {
                String memberId = jwtUtils.getSubjectEvenIfExpired(accessToken);
                String refreshToken = tokenRepository.findRefreshToken(memberId);

                log.info("액세스 토큰 만료 리프레시 발급 = {}", refreshToken);
                // 리프레시 토큰 검증
                if (jwtUtils.validationToken(refreshToken)) {
                    String newAccessToken = jwtUtils.generateAccessToken(memberId);
                    response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken); // 새로운 액세스 토큰 발급

                    authenticateUser(memberId);

                    // 액세스, 리프레시 둘다 만료되었다면 에러를 던지고, 프론트에서 로그인 페이지로 이동
                } else {
                    log.warn("Both access and refresh tokens are expired");
                    throw new MissingTokenException("Access/RefreshToken is expired");
                }

            }
        }

        filterChain.doFilter(request, response);
    }

    // Authentication 등록
    private void authenticateUser(String memberId) {
        UserDetails userDetails = new CustomUserDetails(memberId, "ROLE_USER");
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        //Authentication 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // 소셜 토큰 여부 체크
    private boolean isSocialAccessToken(HttpServletRequest request) {
        String socialTypeHeader = request.getHeader(SOCIAL_TYPE_HEADER);
        return socialTypeHeader != null && socialTypeHeader.equals("kakao");
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
