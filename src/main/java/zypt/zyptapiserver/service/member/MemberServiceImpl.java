package zypt.zyptapiserver.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.SocialAuth;
import zypt.zyptapiserver.dto.member.MemberAndLevelInfoDto;
import zypt.zyptapiserver.domain.enums.RoleType;
import zypt.zyptapiserver.exception.InvalidParamException;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.dto.member.MemberInfoDtoImpl;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.Member.MemberRepositoryImpl;
import zypt.zyptapiserver.repository.SocialAuthRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepositoryImpl repository;
    private final ExpRepository expRepository;
    private final SocialAuthRepository socialAuthRepository;


    @Override
    public Member saveMember(Member member, SocialAuth socialAuth) {
        Member savedMember = repository.save(member);

        LevelExp levelExp = LevelExp.builder()
                .level(1)
                .curExp(0L)
                .build();

        log.info("경험치 초기 데이터 저장");
        savedMember.addLevelExpInfo(levelExp);
        savedMember.addSocialAuth(socialAuth);

        socialAuthRepository.save(socialAuth);
        expRepository.save(levelExp);

        log.info("멤버 생성 성공");
        return savedMember;
    }

    // 멤버 저장
    @Transactional
    public Member saveMember(UserInfo userInfo, SocialType type) {

        log.info("멤버 생성");
        Member member = Member.builder()
                .email(userInfo.getEmail())
                .nickName(UUID.randomUUID().toString())
                .build();

        SocialAuth socialAuth = new SocialAuth(type, userInfo.getId());


        LevelExp levelExp = LevelExp.builder()
                .level(1)
                .curExp(0L)
                .build();


        Member savedMember = repository.save(member);

        log.info("경험치 초기 데이터 저장");
        savedMember.addLevelExpInfo(levelExp);
        savedMember.addSocialAuth(socialAuth);

        socialAuthRepository.save(socialAuth);
        expRepository.save(levelExp);

        log.info("멤버 생성 성공");
        return savedMember;
    }

    // id로 멤버 조회
    public MemberInfoDtoImpl findMember(String memberId) {
        return repository.findMemberInfoById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // social id로 멤버 조회
    @Transactional(readOnly = true)
    public Optional<Member> findOptionalMemberBySocialId(SocialType type, String socialId) {

        return repository.findBySocialId(type, socialId);
    }

    @Transactional(readOnly = true)
    public Member findMemberBySocialId(SocialType type, String socialId) {
        return repository.findBySocialId(type, socialId).orElseThrow(() -> new MemberNotFoundException("멤버 조회 실패"));
    }

    // 닉네임 업데이트
    public void updateNickName(String memberId, String nickName) {
        Member member = repository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException("멤버가 존재하지 않음"));

        log.info("nick = {} -> {}",member.getNickName(), nickName);
        // 닉네임 설정을 안했다면 디폴트로 설정해준다.
        if (!StringUtils.hasText(nickName)) {
            nickName = "user+" + UUID.randomUUID().toString().substring(0, 16);

            // 이전과 같은 닉네임시 예외를 던짐
        } else if (member.getNickName().equals(nickName)) {
            throw new InvalidParamException("이전 닉네임 불가");
        }

        // 더티 체킹으로 업데이트
        member.updateNickName(nickName);
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

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        return repository.findMemberByEmail(email);
    }

    @Override
    public void linkSocialAuth(Member member, SocialAuth socialAuth) {
        member.addSocialAuth(socialAuth);
    }
}
