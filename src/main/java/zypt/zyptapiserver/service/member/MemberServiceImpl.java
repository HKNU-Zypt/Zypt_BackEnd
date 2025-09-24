package zypt.zyptapiserver.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zypt.zyptapiserver.domain.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.exception.InvalidParamException;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.dto.member.MemberInfoDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.Member.MemberRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final ExpRepository expRepository;

    // 멤버 저장
    @Transactional
    public Member saveMember(Member member) {
        log.info("멤버 생성");
        Member savedMember = repository.save(member);

        LevelExp levelExp = LevelExp.builder()
                .level(1)
                .curExp(0L)
                .build();

        log.info("경험치 초기 데이터 저장");
        savedMember.addLevelExpInfo(levelExp);
        expRepository.save(levelExp);

        log.info("멤버 생성 성공");
        return savedMember;
    }

    // id로 멤버 조회
    public MemberInfoDto findMember(String memberId) {
        return repository.findMemberInfoById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // social id로 멤버 조회
    @Transactional(readOnly = true)
    public Member findMemberBySocialId(SocialType type, String socialId) {

        return repository.findBySocialId(type, socialId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // 닉네임 업데이트
    @Transactional
    public void updateNickName(String memberId, String nickName) {
        String memberNickName = repository.findMemberNickName(memberId);

        log.info("nick = {} -> {}",memberNickName, nickName);
        // 닉네임 설정을 안했다면 디폴트로 설정해준다.
        if (!StringUtils.hasText(nickName)) {
            nickName = "user+" + UUID.randomUUID().toString().substring(0, 16);

            // 이전과 같은 닉네임시 예외를 던짐
        } else if (memberNickName.equals(nickName)) {
            throw new InvalidParamException("이전 닉네임 불가");
        }

        repository.updateNickName(memberId, nickName);
    }

    @Override
    public void updateEmail(String memberId, String email) {
        log.info("이메일 업데이트 ");
        Member member = repository
                .findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));

        member.updateEmail(email);

    }

    @Override
    public void saveSocialRefreshToken(String memberId, String refreshToken, SocialType type) {
        log.info("소셜 리프레시 토큰 저장");
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
        log.info("멤버 소셜 리프레시 토큰 조회");

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
        log.info("멤버 소셜 리프레시 토큰 삭제");
        Member member = repository
                .findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
        member.removeSocialRefreshToken();

    }

    @Override
    public MemberAndLevelInfoDto findMemberInfo(String memberId) {
        log.info("멤버 정보 조회");
        return repository.findMemberAndLevelInfo(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 & 레벨 경험치 조회 실패"));
    }

    @Override
    public void deleteMember(String id) {
        log.info("멤버 삭제");
        Member member = repository
                .findMemberById(id)
                .orElseThrow(() -> new MemberNotFoundException("이미 존재하지 않는 회원입니다. "));
        repository.deleteMember(member);
    }

    @Override
    public RoleType findMemberRoleType(String memberId) {
        return repository.findMemberRoleType(memberId);
    }
}
