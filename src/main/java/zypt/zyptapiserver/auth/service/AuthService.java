package zypt.zyptapiserver.auth.service;

import io.jsonwebtoken.Claims;
import zypt.zyptapiserver.auth.exception.InvalidTokenException;
import zypt.zyptapiserver.auth.exception.MissingTokenException;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.MemberRepository;
import zypt.zyptapiserver.repository.RedisRepository;
import zypt.zyptapiserver.util.CookieUtils;
import zypt.zyptapiserver.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final RedisRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final SocialServiceFactory socialServiceFactory;
    private final JwtUtils jwtUtils;

    private static final String SOCIAL_TYPE_HEADER = "SocialType";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public AuthService(RedisRepository tokenRepository, MemberRepository memberRepository, SocialServiceFactory socialServiceFactory, JwtUtils jwtUtils) {
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
        this.socialServiceFactory = socialServiceFactory;
        this.jwtUtils = jwtUtils;
    }


    public void handleAuthenticationFromSocialToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {

        // 팩토리에서 요청에 있는 소셜 타입에 따라 소셜 서비스를 반환
        SocialService socialService = socialServiceFactory.getService(request);
        UserInfo userInfo = socialService.getUserInfo(accessToken);

        if (userInfo == null) {
            throw new InvalidTokenException("social AccessToken is invalid or malformed");
        }

        SocialType type = SocialType.from(request.getHeader(SOCIAL_TYPE_HEADER));

        // 회원가입 하지 않았다면 가입
        Member member = memberRepository.findBySocialId(type, userInfo.getId())
                .orElseGet(() -> {

                    Member newMember = Member.builder()
                            .socialId(userInfo.getId())
                            .email(userInfo.getEmail())
                            .nickName(UUID.randomUUID().toString())
                            .socialType(type)
                            .build();

                    // 회원 db에 저장 및 토큰 생성
                    memberRepository.save(newMember);
                    return newMember;
                });

        String newAccessToken = jwtUtils.generateAccessToken(member.getId());
        String newRefreshToken = findRefreshTokenInRedis(member);
        registryAuthenticatedUser(member.getId(), member.getNickName());

        // 응답에 토큰 삽입
        CookieUtils.addCookie(response, newRefreshToken);
        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken); // 헤더에 액세스 토큰 삽입
    }


    public void authenticateUserFromToken(HttpServletResponse response, String accessToken) {
        Claims claims = jwtUtils.getSubjectEvenIfExpired(accessToken);// 만료된 accessToken의 userId값을 추출

        String id = claims.getSubject();
        String refreshToken = tokenRepository.findRefreshToken(id); ;// redis에 저장된 리프레시 토큰을 찾음

        log.info("액세스 토큰 만료 리프레시 발급 = {}", refreshToken);

        // 리프레시 토큰 검증 (검증 성공시)
        if (jwtUtils.validationToken(refreshToken)) {
            String nickName = claims.get("nickName", String.class);

            String newAccessToken = jwtUtils.generateAccessToken(id);
            response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken); // 새로운 액세스 토큰 발급

            // Authentication 등록
            registryAuthenticatedUser(id);

            // 액세스, 리프레시 둘다 만료되었다면 에러를 던지고, 프론트에서 로그인 페이지로 이동
        } else {
            throw new MissingTokenException("Access/RefreshToken is expired");
        }
    }

    // redis 메모리에서 리프레시토큰 찾고 없다면 생성해서 반환
    private String findRefreshTokenInRedis(Member member) {
        String refreshToken = tokenRepository.findRefreshToken(member.getId());

        // 메모리에 리프레시 토큰이 없다면 생성하고 저장
        if (refreshToken == null) {
            String newRefreshToken = jwtUtils.generateRefreshToken(member.getId());
            tokenRepository.saveRefreshToken(member.getId(), newRefreshToken); // redis에 리프레시 토큰 저장
        }

        return refreshToken;
    }


    // Authentication 등록
    public void registryAuthenticatedUser(String memberId, String nickName) {

        CustomUserDetails userDetails = new CustomUserDetails(memberId, nickName, "ROLE_USER");
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        //Authentication 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // Authentication 등록 db 조회
    public void registryAuthenticatedUser(String memberId) {

        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));

        CustomUserDetails userDetails = new CustomUserDetails(memberId, member.getNickName(), "ROLE_USER");
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        //Authentication 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}





