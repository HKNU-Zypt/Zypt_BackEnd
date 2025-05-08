package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;


    public boolean updateNickName(String id, String nickName) {
        Member member = repository.findById(id).orElseThrow(RuntimeException::new);// 추후 멤버 낫 파인드 예외로

        if (member.getNickName().equals(nickName)) {
            member.updateNickName(nickName);
            return true;
        }

        // 기존과 동일시 false 반환
        return false;
    }




}
