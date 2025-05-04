package zypt.Zyptapiserver.domain;

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
    private String socialId;

    @Builder

    public Member(String id, String name, String socialId) {
        this.id = id;
        this.name = name;
        this.socialId = socialId;
    }
}
