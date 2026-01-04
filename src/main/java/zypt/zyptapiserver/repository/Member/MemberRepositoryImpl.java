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
import zypt.zyptapiserver.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.dto.member.MemberInfoDtoImpl;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.dto.member.QMemberAndLevelInfoDto;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

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


    public Optional<MemberInfoDtoImpl> findMemberInfoById(String memberId) {
        return Optional.ofNullable(queryFactory.select(Projections.constructor(
                        MemberInfoDtoImpl.class,
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
    public Optional<Member> findBySocialId(SocialType provider, String providerId) {
        return em.createQuery(
                        "SELECT s.member FROM SocialAuth s " +
                                "WHERE s.provider = :provider AND s.providerId = :providerId", Member.class)
                .setParameter("provider", provider)
                .setParameter("providerId", providerId)
                .getResultStream() // 바로 스트림으로 받아 처리
                .findFirst();
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
    public void deleteMember(Member member) {
        em.remove(member);
    }


    public RoleType findMemberRoleType(String memberId) {
        return queryFactory.select(qMember.roleType)
                .from(qMember)
                .where(qMember.id.eq(memberId))
                .fetchOne();
    }
}
