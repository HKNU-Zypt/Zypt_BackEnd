package zypt.zyptapiserver.Service;

import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.MemberInfoDto;
import zypt.zyptapiserver.domain.enums.SocialType;


public interface MemberService {

    // 멤버 저장
    public Member saveMember(Member member);

    // id로 멤버 조회
    public MemberInfoDto findMember(String id);

    // social id로 멤버 조회
    public Member findMemberBySocialId(SocialType type, String socialId);

    // 닉네임 업데이트
    public void updateNickName(String id, String nickName);
}
