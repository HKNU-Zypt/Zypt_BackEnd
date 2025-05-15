package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;

    // 멤버 저장
    public Member saveMember(Member member) {
        return repository.save(member);
    }

    // id로 멤버 조회
    public Member findMember(String id) {
        return repository.findMemberById(id).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // social id로 멤버 조회
    @Transactional(readOnly = true)
    public Member findMemberBySocialId(String socialId) {
        return repository.findBySocialId(socialId).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // 닉네임 업데이트
    public boolean updateNickName(String id, String nickName) {
        Member member = repository.findMemberById(id).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));// 추후 멤버 낫 파인드 예외로

        if (member.getNickName().equals(nickName)) {
            member.updateNickName(nickName);
            return true;
        }

        // 기존과 동일시 false 반환
        return false;
    }




}
