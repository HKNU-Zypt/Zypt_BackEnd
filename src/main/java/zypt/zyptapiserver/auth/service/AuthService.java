package zypt.zyptapiserver.auth.service;

import io.jsonwebtoken.Claims;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.auth.exception.InvalidTokenException;
import zypt.zyptapiserver.auth.exception.MissingTokenException;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.MemberRepository;
import zypt.zyptapiserver.repository.RedisRepository;
import zypt.zyptapiserver.util.CookieUtils;
import zypt.zyptapiserver.util.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final RedisRepository redisRepository;
    private final MemberRepository memberRepository;
    private final SocialServiceFactory socialServiceFactory;
    private final JwtUtils jwtUtils;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public AuthService(RedisRepository redisRepository, MemberRepository memberRepository, SocialServiceFactory socialServiceFactory, JwtUtils jwtUtils) {
        this.redisRepository = redisRepository;
        this.memberRepository = memberRepository;
        this.socialServiceFactory = socialServiceFactory;
        this.jwtUtils = jwtUtils;
    }

    /**
     * redis 예외시에도 DB에 멤버가 저장됨, 따라서 트랜잭션을 적용해 회원가입 로직 도중 예외 발생시 저장을 방지
     */
    @Transactional
    public void handleAuthenticationFromSocialToken(HttpServletResponse response, SocialType socialType, String idToken) {
        SocialService socialService = socialServiceFactory.getService(socialType);
        UserInfo userInfo = socialService.getUserInfo(idToken);

        if (userInfo == null) {
            throw new InvalidTokenException("소셜 AccessToken이 유효하지 않거나 형식이 잘못되었습니다");
        }

        // 회원가입 하지 않았다면 가입
        Member member = memberRepository.findBySocialId(socialType, userInfo.getId())
                .orElseGet(() -> {

                    Member newMember = Member.builder()
                            .socialId(userInfo.getId())
                            .email(userInfo.getEmail())
                            .nickName(UUID.randomUUID().toString())
                            .socialType(socialType)
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


    /**
     * 토큰을 통해 인증 객체 등록
     * @param response
     * @param refreshToken
     */
    public void authenticateUserFromToken(HttpServletResponse response, String refreshToken) {
        Claims claims = jwtUtils.getSubjectEvenIfExpired(refreshToken);// 만료된 accessToken의 userId값을 추출

        String id = claims.getSubject();
        String savedRefreshToken = redisRepository.findRefreshToken(id); ;// redis에 저장된 리프레시 토큰을 찾음, 잘못된 id값일시 redis에서 예외를 던짐

        if (!refreshToken.equals(savedRefreshToken)) {
            throw new InvalidTokenException("비정상적인 리프레시 토큰");
        }

        log.info("액세스 토큰 만료 리프레시 발급 = {}", savedRefreshToken);

        // 리프레시 토큰 검증 (검증 성공시)
        if (jwtUtils.validationToken(savedRefreshToken)) {
            String newAccessToken = jwtUtils.generateAccessToken(id);
            response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken); // 새로운 액세스 토큰 발급

            // Authentication 등록
            registryAuthenticatedUser(id);

            // 액세스, 리프레시 둘다 만료되었다면 에러를 던지고, 프론트에서 로그인 페이지로 이동
        } else {
            throw new MissingTokenException("Access,refresh 토큰이 만료되었습니다.");
        }
    }

    // TODO refresh 토큰을 검증하는 건 좋은데 만료시에만 재생성해야함. 따라서 jwtUtils에서 만료 예외만 던지고 여기서 catch해서 생성하게끔 처리
    // redis 메모리에서 리프레시토큰 찾고 없다면 생성해서 반환
    private String findRefreshTokenInRedis(Member member) {
        String refreshToken = redisRepository.findRefreshToken(member.getId());

        // 메모리에 리프레시 토큰이 없거나, 만료되었다면 리프레시 토큰을 재생성하고 저장
        if (refreshToken == null || !jwtUtils.validationToken(refreshToken)) {
            String newRefreshToken = jwtUtils.generateRefreshToken(member.getId());
            redisRepository.saveRefreshToken(member.getId(), newRefreshToken); // redis에 리프레시 토큰 저장
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


    /**
     * 로그아웃으로  리프레시 토큰을 삭제
     * @param memberId
     */
    public void logout(String memberId) {
        redisRepository.deleteRefreshToken(memberId);
    }

    /**
     * 회원탈퇴 (소셜 연동 해제)
     * 카카오는 소셜 아이디만으로 탈퇴 가능
     * 그외 소셜은 액세스토큰이 필요하기에 리프레시 토큰으로 오프라인 액세스토큰 갱신 후 해제 요청
     *
     * @param memberId
     */
    @Transactional
    public void disconnect(String memberId) {
        Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
        SocialService service = socialServiceFactory.getService(member.getSocialType());

        if (member.getSocialType() != SocialType.KAKAO) {
            SocialRefreshToken refreshToken = memberRepository.findSocialRefreshTokenById(memberId)
                            .orElseThrow(() -> new NoSuchElementException("토큰 없음"));

            service.disconnectSocialAccount(refreshToken.getToken());
            memberRepository.deleteRefreshTokenById(memberId);

        } else {
            service.disconnectSocialAccount(member.getSocialId());
        }
        redisRepository.deleteRefreshToken(memberId); // 리프레시 토큰삭제
        memberRepository.deleteMember(member); // 멤버 삭제
    }

}





