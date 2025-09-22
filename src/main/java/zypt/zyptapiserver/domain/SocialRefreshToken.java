package zypt.zyptapiserver.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zypt.zyptapiserver.domain.enums.SocialType;

@Getter
@Entity
@NoArgsConstructor
public class SocialRefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String token;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;


    @QueryProjection
    public SocialRefreshToken(String token, SocialType socialType) {
        this.token = token;
        this.socialType = socialType;
    }

    public void addMember(Member member) {
        this.member = member;
    }
}
