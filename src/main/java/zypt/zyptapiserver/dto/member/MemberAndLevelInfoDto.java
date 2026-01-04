package zypt.zyptapiserver.dto.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberAndLevelInfoDto {
    private String nickName;
    private String email;
    private int level;
    private long exp;

    @QueryProjection
    public MemberAndLevelInfoDto(String nickName, String email, int level, long exp) {
        this.nickName = nickName;
        this.email = email;
        this.level = level;
        this.exp = exp;
    }
}
