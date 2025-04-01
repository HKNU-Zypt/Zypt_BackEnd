package fstt.fsttapiserver.repository;


import fstt.fsttapiserver.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(String id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    // socialId로 멤버 조회
    public Optional<Member> findBySocialId(String socialId) {
        String sql = "select m from Member m where socialId = :socialId";
        Member member = em.createQuery(sql, Member.class)
                .setParameter("socialId", socialId)
                .getSingleResult();
        return Optional.ofNullable(member);
    }

}
