package zypt.zyptapiserver.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MemberV2 extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String nickName;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String socialId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FocusTime> focusTimes = new ArrayList<>();

    @Builder

    public MemberV2(String id, String nickName, String name, String email, SocialType socialType, String socialId) {
        this.id = id;
        this.nickName = nickName;
        this.name = name;
        this.email = email;
        this.socialType = socialType;
        this.socialId = socialId;
    }
}
