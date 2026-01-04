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
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.SocialAuth;
import zypt.zyptapiserver.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.repository.Member.MemberRepository;
import zypt.zyptapiserver.repository.Member.MemberRepositoryImpl;
import zypt.zyptapiserver.service.member.MemberService;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Profile("test")
@SpringBootTest
class MemberRepositoryImplTest {

    @Autowired
    MemberRepositoryImpl repository;

    @Autowired
    MemberService service;

    @Autowired
    MemberRepository repository2;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void init() {
        Member member = Member
                .builder()
                .nickName("aaa")
                .email("aaa@gmail.com")
                .build();
        service.saveMember(member, new SocialAuth(SocialType.NAVER, "123"));
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
        Member saveMember = repository.save(Member.builder().nickName("ㅎㅎ").build());
        Optional<Member> memberById = repository.findMemberById(saveMember.getId());

        Assertions.assertEquals(saveMember, memberById.get());

    }

    @Test
    @Transactional
    @DisplayName("영속화 확인")
    void persistTest() {
        Member member = service.findMemberBySocialId(SocialType.NAVER, "123");

        // 영속화 확인
        assertThat(em.contains(member)).isTrue();
    }


    @Test
    @Transactional
    @DisplayName("존재하지 않는 멤버 조회시 예외 발생 테스트")
    public void notExistMember() throws Exception {
        // then
        assertThatThrownBy(() -> service.findMember("44444"))
                .isInstanceOf(MemberNotFoundException.class);

        assertThatThrownBy(() -> service.findOptionalMemberBySocialId(null, "a"))
                .isInstanceOf(MemberNotFoundException.class);

        assertThatThrownBy(() -> service.findOptionalMemberBySocialId(SocialType.KAKAO, "a"))
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
        assertThatThrownBy(() -> service.findOptionalMemberBySocialId(SocialType.NAVER, "123"))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("경험치까지 같이 조회")
    void findMemberWithLevelExpTest() {
        // given
        Member member = service.findMemberBySocialId(SocialType.NAVER, "123");
        Optional<MemberAndLevelInfoDto> info = repository.findMemberAndLevelInfo(member.getId());

        MemberAndLevelInfoDto memberAndLevelInfoDto = info.get();
        assertThat(memberAndLevelInfoDto.getLevel()).isEqualTo(1);

        log.info("result = {}", memberAndLevelInfoDto);

    }

    @Test
    @Transactional
    @DisplayName("Role 저장 확인")
    void roleCheck() {
        Member member = service.findMemberBySocialId(SocialType.NAVER, "123");
        RoleType memberRoleType = service.findMemberRoleType(member.getId());

        assertThat(member.getRoleType()).isEqualTo(RoleType.ROLE_USER);
        assertThat(memberRoleType.name()).isEqualTo("ROLE_USER");
        log.info(memberRoleType.name());
    }
}