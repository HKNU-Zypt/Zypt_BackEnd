package zypt.zyptapiserver.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import zypt.zyptapiserver.auth.exception.OidcPublicKeyFetchException;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SocialServiceTest {

    @Autowired
    SocialServiceFactory socialServiceFactory;

    @MockitoBean
    OIDCService oidcService;


    @Test
    @DisplayName("OIDC url이 변경되어 캐시 삭제 후 재시도 테스트")
    void deleteOIDCCacheRedo() {
        //given
//        when(oidcService.getJwksUrl(SocialType.KAKAO)).thenThrow(OidcPublicKeyFetchException.class);
        when(oidcService.getJwksUrl(SocialType.KAKAO)).thenReturn("url");
        when(oidcService.getOpenIdPublicKeys(SocialType.KAKAO, "url")).thenThrow(OidcPublicKeyFetchException.class);


        SocialService service = socialServiceFactory.getService(SocialType.KAKAO);

        //when && then
        try {
            UserInfo userInfo = service.getUserInfo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImFiYyJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.h2NDUWpoapMvraokM3bflmUNnbFf7Uum6cUPynN1P1A");

        } catch (RuntimeException e) {
            // AOP가 캐시 삭제후 재시도 했는지 검증
//            verify(oidcService, times(2)).getJwksUrl(SocialType.KAKAO);
            verify(oidcService, times(2)).getOpenIdPublicKeys(SocialType.KAKAO, "url");

        }














    }


}