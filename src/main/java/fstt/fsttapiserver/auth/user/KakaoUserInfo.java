package fstt.fsttapiserver.auth.user;


public class KakaoUserInfo {
    private final String id;
    private final String nickname;
    private final String profileImageUrl;

    public KakaoUserInfo(String id, String nickname, String profileImageUrl) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
