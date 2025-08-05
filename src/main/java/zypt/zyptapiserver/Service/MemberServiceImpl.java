package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zypt.zyptapiserver.auth.exception.InvalidParamException;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.dto.MemberInfoDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.MemberRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;

    // 멤버 저장
    public Member saveMember(Member member) {

        return repository.save(member);
    }

    // id로 멤버 조회
    @Transactional(readOnly = true)
    public MemberInfoDto findMember(String id) {
        Member member = repository.findMemberById(id)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));

        return new MemberInfoDto(member.getId(), member.getNickName(), member.getEmail());
    }

    // social id로 멤버 조회
    @Transactional(readOnly = true)
    public Member findMemberBySocialId(SocialType type, String socialId) {

        return repository.findBySocialId(type, socialId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
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
            throw new InvalidParamException("이전 닉네임 불가");
        }

        member.updateNickName(nickName);

    }

    @Override
    public void updateEmail(String memberId, String email) {
        Member member = repository
                .findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));


        member.updateEmail(email);

    }

    @Override
    public void saveSocialRefreshToken(String memberId, String refreshToken, SocialType type) {
        if (repository.findSocialRefreshTokenById(memberId).isEmpty()) {
            Member member = repository
                    .findMemberById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));

            SocialRefreshToken refreshTokenEntity = new SocialRefreshToken(refreshToken, type);
            member.addSocialRefreshToken(refreshTokenEntity);

            repository.saveSocialRefreshToken(refreshTokenEntity);

            log.info("리프레시 토큰 저장 성공");
            return;
        }

        log.info("리프레시 토큰 이미 존재");
    }

    /**
     * 소셜 리프레시 토큰을 찾는다.
     * @param memberId
     */
    @Override
    public SocialRefreshToken findSocialRefreshToken(String memberId) {
        SocialRefreshToken refreshToken = repository
                .findSocialRefreshTokenById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("소셜 리프레시 토큰 조회 실패"));

        return refreshToken;
    }

    /**
     * 소셜 리프레시 토큰을 삭제한다.
     * @param memberId
     */
    @Override
    public void deleteSocialRefreshToken(String memberId) {
        Member member = repository
                .findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
        member.removeSocialRefreshToken();

    }

    @Override
    public void deleteMember(String id) {
        Member member = repository
                .findMemberById(id)
                .orElseThrow(() -> new MemberNotFoundException("이미 존재하지 않는 회원입니다. "));
        repository.deleteMember(member);
    }


}
