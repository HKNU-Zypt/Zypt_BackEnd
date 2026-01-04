package zypt.zyptapiserver.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zypt.zyptapiserver.domain.enums.SocialType;

@Entity
@Getter
@NoArgsConstructor
public class SocialAuth extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private SocialType provider;

    private String providerId;

    public SocialAuth(SocialType provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }

    public void addMember(Member member) {
        this.member = member;
    }

}
