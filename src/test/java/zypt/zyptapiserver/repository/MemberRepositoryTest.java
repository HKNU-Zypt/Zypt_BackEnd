package zypt.zyptapiserver.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.MemberV1;

import java.util.Optional;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;


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
}