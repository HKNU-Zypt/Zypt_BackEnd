package zypt.zyptapiserver.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.auth.exception.MissingTokenException;
import zypt.zyptapiserver.auth.user.GoogleUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@EqualsAndHashCode
public class GoogleService implements SocialService {

    private final GoogleIdTokenVerifier verifier;

    /**
     * Google API 라이브러리를 이용해서 id_token 검증
     * @param client_id
     */
    public GoogleService(String client_id) {
        try {
            this.verifier = new GoogleIdTokenVerifier
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(List.of(client_id))
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 유저 정보 가져오기
    @Override
    public UserInfo getUserInfo(String token) {
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken == null) {
                throw new MissingTokenException("유효하지 않은 id_token");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            log.info("payload = {}", payload.toPrettyString());

            return new GoogleUserInfo(payload.getSubject(), payload.getEmail());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("id token 검증 중 오류 발생", e);
        }

    }
}
