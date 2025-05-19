package zypt.zyptapiserver.repository;


import zypt.zyptapiserver.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<Member> findMemberById(String id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    // socialId로 멤버 조회
    @Transactional(readOnly = true)
    public Optional<Member> findBySocialId(String socialId) {
        String sql = "select m from Member m where socialId = :socialId";
        List<Member> member = em.createQuery(sql, Member.class)
                .setParameter("socialId", socialId)
                .getResultList();

        return member.stream().findFirst();
    }




}
