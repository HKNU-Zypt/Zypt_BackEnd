package zypt.zyptapiserver.auth.service;

import zypt.zyptapiserver.auth.user.UserInfo;

public interface SocialService {

    public UserInfo getUserInfo(String token);

    public void disconnectSocialAccount(String refreshToken);
}
