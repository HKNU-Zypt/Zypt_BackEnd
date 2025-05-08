package zypt.zyptapiserver.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    private String id;
    private String name;
    private String nickName;
    private String socialId;

    @Builder
    public Member(String id, String name, String nickName, String socialId) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.socialId = socialId;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }
}
