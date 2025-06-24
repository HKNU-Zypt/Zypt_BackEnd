package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.Service.MemberServiceImpl;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;

import java.util.Optional;

@Slf4j
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Autowired
    MemberService service;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("부정확한 멤버 저장시 JPA 반응 테스트")
    void saveMemberTest() {
        Member saveMember = repository.save(new Member());
    }

    @Test
    @DisplayName("")
    void saveMemberAndFindMemberTest() {
        Member saveMember = repository.save(Member.builder().nickName("ㅎㅎ").socialId("123423").build());
        Optional<Member> memberById = repository.findMemberById(saveMember.getId());

        Assertions.assertEquals(saveMember, memberById.get());

    }

    @Test
    @DisplayName("영속화 확인")
    void persistTest() {
        service.deleteSocialRefreshToken("feb50faf-d69d-47a7-a303-faa29a9d01af");


    }
}