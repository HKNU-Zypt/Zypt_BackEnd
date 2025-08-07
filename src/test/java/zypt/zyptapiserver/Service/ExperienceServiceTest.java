package zypt.zyptapiserver.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.enums.SocialType;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ExperienceServiceTest {

    @Autowired
    ExperienceService experienceService;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void init() {
        Member member = memberService.saveMember(Member.builder().email("abc").socialId("123").nickName("abcd").socialType(SocialType.KAKAO).build());
    }

    @Test
    @Transactional
    void run() {

        Member member = memberService.findMemberBySocialId(SocialType.KAKAO, "123");

        experienceService.applyExperience(member.getId(), 500);

    }

}