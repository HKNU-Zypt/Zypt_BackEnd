package zypt.zyptapiserver.auth.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

// 직렬화를 설정한다.
// 이유는 securityContext가 기본적으로 세션(HttpSession)에 저장되기 때문에
// HttpSession은 WAS가 관리하므로 세션 저장시 객체 직렬화가 발생한다.
// 서버 재시작, 클러스터링, Redis 세션 저장등을 쓰면 에러 발생할 수 있으므로 수동으로
// 직렬화 UID를 작성해준다.

// 수동으로 설정하면 기존 멤버 변수 타입이 변경될때를 제외하고는 역직렬화 시 영향이 없다.
public class CustomUserDetails implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 7385011047362978493L;

    private final String id;
    private final Collection<GrantedAuthority> authorities;


    public CustomUserDetails(String id, String roles) {
        this.id = id;
        this.authorities = createAuthorities(roles);
    }

    private Collection<GrantedAuthority> createAuthorities(String roles) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles.split(",")) {
            if (!StringUtils.hasText(role)) continue;
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
