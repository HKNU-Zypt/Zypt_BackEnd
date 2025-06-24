package zypt.zyptapiserver.domain;

import jakarta.persistence.*;
import lombok.Getter;
import zypt.zyptapiserver.domain.enums.SocialType;

@Getter
@Entity
public class SocialRefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String token;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    public SocialRefreshToken() {
    }

    public SocialRefreshToken(String token, SocialType socialType) {
        this.token = token;
        this.socialType = socialType;
    }

    public void addMember(Member member) {
        this.member = member;
    }
}
