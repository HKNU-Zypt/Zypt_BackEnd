package zypt.zyptapiserver.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.QLevelExp;

@Repository
@RequiredArgsConstructor
public class ExpRepository {

    private final JPAQueryFactory queryFactory;
    private final QLevelExp qLevelExp = QLevelExp.levelExp;
    private final EntityManager em;

    @Transactional
    public void save(LevelExp levelExp) {
        em.persist(levelExp);
    }


    public LevelExp findById(String memberId) {
        return queryFactory
                .selectFrom(qLevelExp)
                .where(qLevelExp.member.id.eq(memberId))
                .fetchOne();
    }

    @Transactional
    public void updateLevelAndExp(int level, long exp) {
        long cnt = queryFactory.update(qLevelExp)
                .set(qLevelExp.level, level)
                .set(qLevelExp.curExp, exp)
                .execute();
    }



}
