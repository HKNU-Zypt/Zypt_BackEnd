package zypt.zyptapiserver.service.member;

import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.domain.dto.member.MemberInfoDto;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.domain.enums.SocialType;


public interface MemberService {

    // 멤버 저장
    Member saveMember(Member member);

    // id로 멤버 조회
    MemberInfoDto findMember(String memberId);

    // social id로 멤버 조회
    Member findMemberBySocialId(SocialType type, String socialId);

    // 닉네임 업데이트
    void updateNickName(String memberId, String nickName);

    // 회원 탈퇴
    void deleteMember(String memberId);

    void updateEmail(String memberId, String email);

    void saveSocialRefreshToken(String memberId, String refreshToken, SocialType type);

    SocialRefreshToken findSocialRefreshToken(String memberId);

    void deleteSocialRefreshToken(String memberId);

    MemberAndLevelInfoDto findMemberInfo(String memberId);

    RoleType findMemberRoleType(String memberId);
}
