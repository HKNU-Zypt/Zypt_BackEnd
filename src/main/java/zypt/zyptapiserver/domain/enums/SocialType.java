package zypt.zyptapiserver.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SocialType {
    KAKAO("kakao", "https://kauth.kakao.com"),
    GOOGLE("google", null),
    NAVER("naver", null);

    private final String type;
    private final String iss;

    SocialType(String type, String iss) {
        this.type = type;
        this.iss = iss;
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
