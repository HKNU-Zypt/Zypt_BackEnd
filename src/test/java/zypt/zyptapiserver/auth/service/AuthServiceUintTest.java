package zypt.zyptapiserver.auth.service;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.exception.InvalidTokenException;
import zypt.zyptapiserver.exception.MissingTokenException;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.repository.Member.MemberRepositoryImpl;
import zypt.zyptapiserver.repository.RedisRepository;
import zypt.zyptapiserver.util.JwtUtils;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUintTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private MemberRepositoryImpl memberRepositoryImpl;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SocialServiceFactory factory;

    @Mock
    private HttpServletResponse response;


    @Test
    @DisplayName("소셜 토큰 전송으로 회원 가입 및 생성, 액세스 리프레시 토큰 반환")
    void loginSocialTokenTest() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String accessToken = "mock-kakao-token";
        SocialService socialService = mock(SocialService.class);
        UserInfo kakaoUserInfo = new UserInfo("abc", "abc@google.com");
        UsernamePasswordAuthenticationToken authenticationToken = mock(UsernamePasswordAuthenticationToken.class);
        Member member = new Member("abc", "he", "abc@google.com");

        when(factory.getService(socialType)).thenReturn(socialService);
        when(socialService.getUserInfo(accessToken)).thenReturn(kakaoUserInfo);
        when(memberRepositoryImpl.findBySocialId(socialType, kakaoUserInfo.getId())).thenReturn(Optional.of(member));
        when(jwtUtils.generateAccessToken(member.getId())).thenReturn("mock-access-token");
        when(jwtUtils.generateRefreshToken(member.getId())).thenReturn("mock-refresh-token");
        when(memberRepositoryImpl.findMemberRoleType(member.getId())).thenReturn(RoleType.ROLE_USER);

        // when
        authService.handleAuthenticationFromSocialToken(response, socialType, accessToken);

        //then
        verify(factory).getService(socialType);
        verify(socialService).getUserInfo(accessToken);
        verify(memberRepositoryImpl).findBySocialId(socialType, kakaoUserInfo.getId());
        verify(jwtUtils).generateAccessToken(member.getId());
        verify(jwtUtils).generateRefreshToken(member.getId());

        verify(redisRepository).saveRefreshToken(member.getId(), "mock-refresh-token");

        verify(response).addHeader(eq("Set-Cookie"), anyString());
        verify(response).addHeader(eq("Authorization"), eq("Bearer mock-access-token"));
    }

    @Test
    @DisplayName("소셜 토큰이 비정상으로 userInfo를 가져오지 못하여 예외 발생")
    void loginFailSocialTokenTest() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String accessToken = "mock-kakao-token";
        SocialService socialService = mock(SocialService.class);

        when(factory.getService(socialType)).thenReturn(socialService);
        when(socialService.getUserInfo(accessToken)).thenReturn(null);

        //when & then
        Assertions.assertThatThrownBy(()
                        -> authService.handleAuthenticationFromSocialToken(response, socialType, accessToken))
                .isInstanceOf(InvalidTokenException.class);



        verify(factory).getService(socialType);
        verify(socialService).getUserInfo(accessToken);
    }


    @Test
    @DisplayName("액세스 토큰 만료로 리프레시 토큰을 통해 재발급")
    void loginJwtTokenTest() {
        // given
        String refreshToken = "mock-refresh-token";
        Claims claims = mock(Claims.class);
        Member member = new Member("abc", "gg", "fdas");


        when(jwtUtils.getSubjectEvenIfExpired(refreshToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("abc");
        when(redisRepository.findRefreshToken("abc")).thenReturn("mock-refresh-token");
        when(jwtUtils.generateAccessToken("abc")).thenReturn("mock-newAccess-token");
        when(jwtUtils.validationToken("mock-refresh-token")).thenReturn(true);
//        when(memberRepository.findMemberById("abc")).thenReturn(Optional.of(member));
        when(memberRepositoryImpl.findMemberRoleType(member.getId())).thenReturn(RoleType.ROLE_USER);

        // when
        authService.authenticateUserFromToken(response, refreshToken);

        //then
        verify(jwtUtils).validationToken(refreshToken);
        verify(response).addHeader(eq("Authorization"), anyString());

    }

    @Test
    @DisplayName("액세스 토큰 만료로 리프레시 토큰을 통해 재발급")
    void loginFailJwtTokenTest() {
        // given
        String refreshToken = "mock-refresh-token";
        Claims claims = mock(Claims.class);


        when(jwtUtils.getSubjectEvenIfExpired(refreshToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("abc");
        when(redisRepository.findRefreshToken("abc")).thenReturn("mock-not-refresh-token");


        //when & then
        Assertions.assertThatThrownBy(() ->
                        authService.authenticateUserFromToken(response, refreshToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("액세스 토큰 만료로 리프레시 토큰을 통한 재발급 실패 (리프레시 토큰도 만료)")
    void loginFailJwtRefreshTokenExpiredTest() {
        // given
        String refreshToken = "mock-refresh-token";
        Claims claims = mock(Claims.class);


        when(jwtUtils.getSubjectEvenIfExpired(refreshToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("abc");
        when(redisRepository.findRefreshToken("abc")).thenReturn("mock-refresh-token");
        when(jwtUtils.validationToken(refreshToken)).thenReturn(false);


        //when & then
        Assertions.assertThatThrownBy(() ->
                        authService.authenticateUserFromToken(response, refreshToken))
                .isInstanceOf(MissingTokenException.class);
    }

    @Test
    @DisplayName("Authentcation 등록")
    void registryAuthenticatedUserTest() {

        //given
        String memberId = "abc";
//        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(new Member(memberId, "hh", null, null, null)));

        when(memberRepositoryImpl.findMemberRoleType(memberId)).thenReturn(RoleType.ROLE_USER);
        //when
        authService.registryAuthenticatedUser(memberId);

        //then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.junit.jupiter.api.Assertions.assertNotNull(authentication);
        org.junit.jupiter.api.Assertions.assertEquals(memberId, authentication.getName());

    }

}