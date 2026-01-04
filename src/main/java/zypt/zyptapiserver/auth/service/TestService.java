package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;

@Service
@Slf4j
@SocialIdentifier(SocialType.TEST)
@RequiredArgsConstructor
public class TestService implements SocialService {

    private final ObjectMapper objectMapper;

    @Override
    public UserInfo getUserInfo(String token) {
        try {
            JsonNode jsonNode = objectMapper.readTree(token);

            String socialId = jsonNode.get("id").asText();
            String email = jsonNode.get("email").asText();

            return new UserInfo(socialId, email);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnectSocialAccount(String refreshToken) {

    }
}


