package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.Service.MemberServiceImpl;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Autowired
    MemberService service;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void init() {
        Member member = Member
                .builder()
                .nickName("aaa")
                .socialType(SocialType.NAVER)
                .email("aaa@gmail.com")
                .socialId("123").build();
        Member member1 = service.saveMember(member);

        service.saveSocialRefreshToken(member1.getId(), "abc", SocialType.NAVER);
    }

    @Test
    @Transactional
    @DisplayName("부정확한 멤버 저장시 JPA 반응 테스트")
    void saveMemberTest() {
        assertThatThrownBy(() -> {
            repository.save(new Member());
            em.flush();
        }).isInstanceOf(JDBCException.class);
    }

    @Test
    @Transactional
    @DisplayName("")
    void saveMemberAndFindMemberTest() {
        Member saveMember = repository.save(Member.builder().nickName("ㅎㅎ").socialId("123423").build());
        Optional<Member> memberById = repository.findMemberById(saveMember.getId());

        Assertions.assertEquals(saveMember, memberById.get());

    }

    @Test
    @Transactional
    @DisplayName("영속화 확인")
    void persistTest() {
        Member member = service.findMemberBySocialId(SocialType.NAVER, "123");

        SocialRefreshToken refreshToken = service.findSocialRefreshToken(member.getId());

        // 빈값 확인
        assertThat(refreshToken.getToken()).isNotNull();

        // 영속화 확인
        assertThat(em.contains(refreshToken)).isTrue();
    }



    @Test
    @Transactional
    @DisplayName("존재하지 않는 멤버 조회시 예외 발생 테스트")
    public void notExistMember() throws Exception {
        // then
        assertThatThrownBy(() -> service.findMember("44444"))
                .isInstanceOf(MemberNotFoundException.class);

        assertThatThrownBy(() -> service.findMemberBySocialId(null, "a"))
                .isInstanceOf(MemberNotFoundException.class);

        assertThatThrownBy(() -> service.findMemberBySocialId(SocialType.KAKAO, "a"))
                .isInstanceOf(MemberNotFoundException.class);

    }

    @Test
    @Transactional
    @DisplayName("멤버 삭제 테스트")
    public void deleteMemberSuccessTest() throws Exception {
        //given
        Member member = service.findMemberBySocialId(SocialType.NAVER, "123");

        // when
        service.deleteMember(member.getId());

        //then
        assertThatThrownBy(() -> service.findMemberBySocialId(SocialType.NAVER, "123"))
                .isInstanceOf(MemberNotFoundException.class);
    }
}