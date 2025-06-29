package zypt.zyptapiserver.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SocialType {
    KAKAO("kakao", "https://kauth.kakao.com", "https://kapi.kakao.com/v1/user/unlink"),
    GOOGLE("google", "https://accounts.google.com" , null),
    NAVER("naver", "https://nid.naver.com", null);

    private final String type;
    private final String iss;
    private final String unlink;

    SocialType(String type, String iss, String unlink) {
        this.type = type;
        this.iss = iss;
        this.unlink = unlink;
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
