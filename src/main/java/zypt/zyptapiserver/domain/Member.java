package zypt.zyptapiserver.domain;

import com.fasterxml.uuid.Generators;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.*;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Member extends BaseTimeEntity {

    @Id
    private String id;
    private String nickName;
    private String email;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String socialId;


    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private LevelExp levelExp;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private SocialRefreshToken socialRefreshToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FocusTime> focusTimes = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate().toString();
        }
    }

    @Builder
    @QueryProjection
    public Member(String id, String nickName, String email, SocialType socialType, String socialId) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.socialType = socialType;
        this.socialId = socialId;
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
        focusTime.setMember(this);
    }

    public void addSocialRefreshToken(SocialRefreshToken token) {
        this.socialRefreshToken = token;
        token.addMember(this);
    }

    public void removeSocialRefreshToken() {
        this.socialRefreshToken = null;
    }

    public void addLevelExpInfo(LevelExp levelExp) {
        this.levelExp = levelExp;
        levelExp.updateMember(this);
    }
}
