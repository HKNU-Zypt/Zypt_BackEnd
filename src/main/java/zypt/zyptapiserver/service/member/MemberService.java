package zypt.zyptapiserver.service.member;

import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialAuth;
import zypt.zyptapiserver.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.dto.member.MemberInfoDtoImpl;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.Optional;


public interface MemberService {

    // 멤버 저장
    Member saveMember(Member member, SocialAuth socialAuth);

    Member saveMember(UserInfo userInfo, SocialType type);

    // id로 멤버 조회
    MemberInfoDtoImpl findMember(String memberId);

    // social id로 멤버 조회
    Optional<Member> findOptionalMemberBySocialId(SocialType type, String socialId);

    Member findMemberBySocialId(SocialType type, String socialId);

    // 닉네임 업데이트
    void updateNickName(String memberId, String nickName);

    // 회원 탈퇴
    void deleteMember(String memberId);

    void updateEmail(String memberId, String email);

    MemberAndLevelInfoDto findMemberInfo(String memberId);

    RoleType findMemberRoleType(String memberId);

    Optional<Member> findMemberByEmail(String email);

    void linkSocialAuth(Member member, SocialAuth socialAuth);
}
