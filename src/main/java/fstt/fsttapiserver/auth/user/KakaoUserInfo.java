package fstt.fsttapiserver.auth.user;


import lombok.Getter;

public class KakaoUserInfo implements UserInfo {
    private final String id;
    private final String nickname;
    private final String profileImageUrl;

    public KakaoUserInfo(String id, String nickname, String profileImageUrl) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getNickName() {
        return nickname;
    }
}
