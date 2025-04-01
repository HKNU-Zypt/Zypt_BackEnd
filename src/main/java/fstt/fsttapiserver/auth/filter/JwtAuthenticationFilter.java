package fstt.fsttapiserver.auth.filter;

import fstt.fsttapiserver.auth.exception.InvalidTokenException;
import fstt.fsttapiserver.auth.exception.MissingTokenException;
import fstt.fsttapiserver.auth.service.KakaoService;
import fstt.fsttapiserver.auth.service.UserDetailsServiceImpl;
import fstt.fsttapiserver.auth.user.CustomUserDetails;
import fstt.fsttapiserver.auth.user.KakaoUserInfo;
import fstt.fsttapiserver.domain.Member;
import fstt.fsttapiserver.repository.MemberRepository;
import fstt.fsttapiserver.repository.RedisRepository;
import fstt.fsttapiserver.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final MemberRepository memberRepository;
    private final RedisRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);

        if (accessToken == null) {
            throw new MissingTokenException("AccessToken is required");
        }

        // 소셜 토큰일 경우
        if (isSocialAccessToken(request)) {
            KakaoUserInfo kakaoUserInfo = KakaoService.getKakaoUserInfo(accessToken);

            if (kakaoUserInfo == null) {
                throw new InvalidTokenException("social AccessToken is invalid or malformed");
            }


            Member member = memberRepository.findBySocialId(kakaoUserInfo.getId()).orElse(null);

            // 회원가입 하지 않았다면 가입
            if (member == null) {
                member = Member.builder()
                        .id(String.valueOf(UUID.randomUUID()))
                        .name(kakaoUserInfo.getNickname())
                        .build();
            }


            // 회원 db에 저장 및 토큰 생성
            userDetailsService.saveMember(member);
            String newAccessToken = jwtUtils.generateAccessToken(member.getId());
            String newRefreshToken = jwtUtils.generateRefreshToken(member.getId());

            tokenRepository.saveRefreshToken(member.getId(), newRefreshToken); // redis에 리프레시 토큰 저장

            // 응답에 토큰 삽입
            response.addCookie(new Cookie("refreshToken", newRefreshToken));
            response.addHeader("Authorization", newAccessToken);

            // 자체 JWT 토큰일 경우
        } else {
            // 검증 성공 시
            if (jwtUtils.validationToken(accessToken)) {
                String userId = jwtUtils.extractUserId(accessToken);
                CustomUserDetails userDetails = new CustomUserDetails(userId, "ROLE_USER");

                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //Authentication 저장
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // 액세스 토큰 만료 시 claims를 반환
            } else {
                String memberId = jwtUtils.getSubjectEvenIfExpired(accessToken);
                String refreshToken = tokenRepository.findRefreshToken(memberId);

                // 리프레시 토큰 검증, 실패시
                if (jwtUtils.validationToken(refreshToken)) {
                    String newAccessToken = jwtUtils.generateAccessToken(memberId);
                    response.addHeader("Authorization", newAccessToken); // 새로운 액세스 토큰 발급

                    // 액세스, 리프레시 둘다 만료되었다면 에러를 던지고, 프론트에서 로그인 페이지로 이동
                } else {
                    throw new MissingTokenException("Access/RefreshToken is expired");
                }

            }
        }


        filterChain.doFilter(request, response);
    }

    // 소셜 토큰 여부 체크
    private boolean isSocialAccessToken(HttpServletRequest request) {
        return request.getHeader("SocialType").equals("kakao");
    }

    // jwt 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
