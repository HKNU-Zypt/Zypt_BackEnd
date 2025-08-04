package zypt.zyptapiserver.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LevelExp extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private int level;
    private long curExp;

    @Builder
    public LevelExp(Member member, int level, long curExp) {
        this.member = member;
        this.level = level;
        this.curExp = curExp;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
