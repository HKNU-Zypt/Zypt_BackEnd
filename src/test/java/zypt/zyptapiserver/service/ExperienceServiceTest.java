package zypt.zyptapiserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialAuth;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.service.exp.ExperienceService;
import zypt.zyptapiserver.service.member.MemberService;

@SpringBootTest
class ExperienceServiceTest {

    @Autowired
    ExperienceService experienceService;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void init() {
        Member member = memberService.saveMember(Member.builder().email("abc").nickName("abcd").build(), new SocialAuth(SocialType.KAKAO, "123"));
    }

    @Test
    @Transactional
    void run() {
        Member member = memberService.findOptionalMemberBySocialId(SocialType.KAKAO, "123").get();
        experienceService.applyExperience(member.getId(), 500);
    }

}