package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.MemberRepository;

import java.util.UUID;

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
    @Transactional(readOnly = true)
    public Member findMember(String id) {
        return repository.findMemberById(id).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // social id로 멤버 조회
    @Transactional(readOnly = true)
    public Member findMemberBySocialId(String socialId) {
        return repository.findBySocialId(socialId).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // 닉네임 업데이트
    public void updateNickName(String id, String nickName) {
        Member member = repository.findMemberById(id)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));

        // 닉네임 설정을 안했다면 디폴트로 설정해준다.
        if (!StringUtils.hasText(nickName)) {
            nickName = "user+" + UUID.randomUUID().toString().substring(0, 16);

            // 이전과 같은 닉네임시 예외를 던짐
        } else if (member.getNickName().equals(nickName)) {
            throw new IllegalArgumentException("이전 닉네임 불가");
        }

        member.updateNickName(nickName);

    }
}
