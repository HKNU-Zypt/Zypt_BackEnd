package zypt.zyptapiserver.auth.service;

import zypt.zyptapiserver.auth.user.UserInfo;

import java.io.IOException;

public interface SocialService {

    public UserInfo getUserInfo(String token);

}
