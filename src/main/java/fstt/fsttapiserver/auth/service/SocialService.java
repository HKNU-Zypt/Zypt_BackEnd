package fstt.fsttapiserver.auth.service;

import fstt.fsttapiserver.auth.user.UserInfo;

public interface SocialService {

    public UserInfo getUserInfo(String accessToken);

}
