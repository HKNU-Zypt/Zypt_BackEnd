package zypt.zyptapiserver.auth.user;

import lombok.ToString;

@ToString
public class TestUserInfo implements UserInfo {

    private final String id;
    private final String email;

    public TestUserInfo(String id, String email) {
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
