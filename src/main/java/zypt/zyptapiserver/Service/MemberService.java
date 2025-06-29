package zypt.zyptapiserver.Service;

import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.dto.MemberInfoDto;
import zypt.zyptapiserver.domain.enums.SocialType;


public interface MemberService {

    // 멤버 저장
    Member saveMember(Member member);

    // id로 멤버 조회
    MemberInfoDto findMember(String id);

    // social id로 멤버 조회
    Member findMemberBySocialId(SocialType type, String socialId);

    // 닉네임 업데이트
    void updateNickName(String id, String nickName);

    // 회원 탈퇴
    void deleteMember(String id);

    void updateEmail(String memberId, String email);

    void saveSocialRefreshToken(String memberId, String refreshToken, SocialType type);

    SocialRefreshToken findSocialRefreshToken(String memberId);

    void deleteSocialRefreshToken(String memberId);
}
