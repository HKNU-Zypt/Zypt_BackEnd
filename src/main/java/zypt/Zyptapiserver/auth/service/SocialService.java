package zypt.Zyptapiserver.auth.service;

import zypt.Zyptapiserver.auth.user.UserInfo;

public interface SocialService {

    public UserInfo getUserInfo(String accessToken);

}
