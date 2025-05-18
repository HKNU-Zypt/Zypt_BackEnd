package zypt.zyptapiserver.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false) // Member 필드만 고려
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String nickName;
    private String socialId;

    @Builder
    public Member(String name, String nickName, String socialId) {
        this.name = name;
        this.nickName = nickName;
        this.socialId = socialId;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }
}
