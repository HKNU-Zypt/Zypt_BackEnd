package fstt.fsttapiserver.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SocialType {
    KAKAO("kakao"),
    GOOGLE("google"),
    NAVER("naver");

    private final String type;

    SocialType(String type) {
        this.type = type;
    }

    public static boolean checkSocialType(String type) {
        for (SocialType provider : values()) {
            if (provider.type.equalsIgnoreCase(type)) {
                return true;
            }
        }

        return false;
    }

    public static SocialType from(String requestSocialType) {
        return Arrays.stream(values())
                .filter(socialType -> socialType.getType().equals(requestSocialType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown social requestSocialType : " + requestSocialType));
    }

}
