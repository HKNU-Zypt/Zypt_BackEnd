package zypt.zyptapiserver.auth.user;

public class GoogleUserInfo implements UserInfo {
    private final String id;
    private final String email;

    public GoogleUserInfo(String id, String email) {
        this.id = id;
        this.email = email;
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
        return "";
    }

    @Override
    public String getEmail() {
        return email;
    }
}
