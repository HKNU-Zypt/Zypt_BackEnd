package zypt.zyptapiserver.domain;

import com.fasterxml.uuid.Generators;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.*;
import zypt.zyptapiserver.domain.enums.RoleType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false) // baseEntity 동일성 판단에 제외
public class Member extends BaseTimeEntity {

    @Id
    private String id;
    private String nickName;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType roleType;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, optional = false)
    private LevelExp levelExp;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FocusTime> focusTimes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAuth> socialAuths = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate().toString();
        }
    }

    @Builder
    @QueryProjection
    public Member(String id, String nickName, String email) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.roleType = RoleType.ROLE_USER;
    }


    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
    // 연관관계 편의 메서드
    public void addFocusTimes(FocusTime focusTime) {
        focusTimes.add(focusTime);
        focusTime.addMember(this);
    }

    public void addLevelExpInfo(LevelExp levelExp) {
        this.levelExp = levelExp;
        levelExp.addMember(this);
    }

    public void addSocialAuth(SocialAuth socialAuth) {
        this.socialAuths.add(socialAuth);
        socialAuth.addMember(this);
    }
}
