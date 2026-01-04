package zypt.zyptapiserver.service.member;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialAuth;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;


    @Autowired
    EntityManager em;

    @BeforeEach
    void init() {
    }

    @Test
    @Transactional
    void delete() {
        Member member = memberService.saveMember(Member.builder().email("abc@gmail.com").nickName("greregre").build(), new SocialAuth(SocialType.TEST, "gregree42342"));
        memberService.deleteMember(member.getId());


    }
}