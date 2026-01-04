package zypt.zyptapiserver.auth.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {
    private final String id;
    private final String email;

}
