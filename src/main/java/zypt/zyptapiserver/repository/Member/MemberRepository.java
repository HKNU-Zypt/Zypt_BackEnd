package zypt.zyptapiserver.repository.Member;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import zypt.zyptapiserver.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.QLevelExp;
import zypt.zyptapiserver.domain.QMember;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.domain.dto.member.MemberInfoDto;
import zypt.zyptapiserver.domain.dto.member.QMemberAndLevelInfoDto;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private final QMember qMember = QMember.member;
    private final QLevelExp qLevelExp = QLevelExp.levelExp;


    @Transactional
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Transactional
    public Optional<Member> findMemberById(String memberId) {
        return Optional.ofNullable(em.find(Member.class, memberId));
    }


    public Optional<MemberInfoDto> findMemberInfoById(String memberId) {
        return Optional.ofNullable(queryFactory.select(Projections.constructor(
                        MemberInfoDto.class,
                        qMember.id,
                        qMember.nickName,
                        qMember.email

                ))
                .from(qMember)
                .where(qMember.id.eq(memberId))
                .fetchOne()
        );
    }

    // socialId로 멤버 조회
    @Transactional(readOnly = true)
    public Optional<Member> findBySocialId(SocialType socialType, String socialId) {
        String sql = "select m from Member m where socialType = :socialType and socialId = :socialId";
        List<Member> member = em.createQuery(sql, Member.class)
                .setParameter("socialType", socialType)
                .setParameter("socialId", socialId)
                .getResultList();

        return member.stream().findFirst();
    }

    @Transactional
    public void saveSocialRefreshToken(SocialRefreshToken refreshToken) {
        em.persist(refreshToken);
    }

    @Transactional
    public Optional<SocialRefreshToken> findSocialRefreshTokenById(String memberId) {
        String sql = "select sr from SocialRefreshToken sr where sr.member.id = :memberId";
        List<SocialRefreshToken> refreshTokens = em.createQuery(sql, SocialRefreshToken.class)
                .setParameter("memberId", memberId)
                .getResultList();

        return refreshTokens.stream().findFirst();
    }


    public Optional<MemberAndLevelInfoDto> findMemberAndLevelInfo(String memberId) {
        return Optional.ofNullable(
                queryFactory.select(new QMemberAndLevelInfoDto(
                        qMember.nickName,
                        qMember.email,
                        qLevelExp.level,
                        qLevelExp.curExp
                ))
                .from(qMember)
                .innerJoin(qMember.levelExp, qLevelExp)
                .where(qMember.id.eq(memberId))
                .fetchOne()
        );
    }


    @Transactional
    public void updateNickName(String memberId, String nickName) {
        long execute = queryFactory.update(qMember)
                .set(qMember.nickName, nickName)
                .where(qMember.id.eq(memberId))
                .execute();

        if (execute != 1) {
            throw new IllegalStateException("업데이트 실패");
        }
    }

    @Transactional
    public void deleteRefreshTokenById(String memberId) {
        SocialRefreshToken refreshToken = findSocialRefreshTokenById(memberId).get();
        em.remove(refreshToken);

//        em.createQuery("DELETE FROM SocialRefreshToken s WHERE s.member.id = :id")
//                .setParameter("id", memberId)
//                .executeUpdate();
    }


    @Transactional
    public void deleteMember(Member member) {
        em.remove(member);
    }


    public String findMemberNickName(String memberId) {
        return queryFactory.select(qMember.nickName)
                .from(qMember)
                .where(qMember.id.eq(memberId))
                .fetchOne();
    }
}
