package zypt.zyptapiserver.repository.Member;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.dto.member.MemberAndLevelInfoDto2;
import zypt.zyptapiserver.dto.member.MemberInfoDto;
import zypt.zyptapiserver.domain.enums.RoleType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {


    @Query("SELECT m.roleType FROM Member m WHERE m.id = :id")
    Optional<RoleType> findRoleTypeById(@Param("id") String id);

    @Query("SELECT m.id AS memberId, m.nickName AS nickName, m.email AS email FROM Member m WHERE m.id = :id")
    Optional<MemberInfoDto> findMemberInfoById(@Param("id") String id);

    @Query("SELECT m FROM Member m JOIN m.socialAuths s " +
            "WHERE s.provider = :provider AND s.providerId = :providerId")
    Optional<Member> findMemberBySocialInfo(@Param("provider") String provider, @Param("providerId") String providerId);

    @Query("SELECT m.nickName AS nickName, m.email AS email, l.level AS level, l.curExp AS exp FROM Member m JOIN m.levelExp l WHERE m.id = :id")
    Optional<MemberAndLevelInfoDto2> findMemberAndLevelInfoById(@Param("id") String id);


}
