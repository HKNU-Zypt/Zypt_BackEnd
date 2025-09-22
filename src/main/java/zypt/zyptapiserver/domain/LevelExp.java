package zypt.zyptapiserver.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;

@Entity
@Getter
@NoArgsConstructor
public class LevelExp {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Column(name = "cur_level")
    private int level;
    @Column(name = "cur_exp")
    private long curExp;

    @Builder
    @QueryProjection
    public LevelExp(Member member, int level, long curExp) {
        this.member = member;
        this.level = level;
        this.curExp = curExp;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateExpAndLevel(LevelExpInfo info) {
        level = info.level();
        curExp = (long) info.exp();
    }
}
