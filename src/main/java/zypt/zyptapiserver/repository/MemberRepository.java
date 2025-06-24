package zypt.zyptapiserver.repository;


import zypt.zyptapiserver.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Transactional
    public Optional<Member> findMemberById(String memberId) {
        return Optional.ofNullable(em.find(Member.class, memberId));
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

    @Transactional
    public void deleteRefreshTokenById(String memberId) {
        em.createQuery("DELETE FROM SocialRefreshToken s WHERE s.member.id = :id")
                .setParameter("id", memberId)
                .executeUpdate();
    }
}
