package zypt.zyptapiserver.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.auth.exception.FocusTimeNotFoundException;
import zypt.zyptapiserver.domain.*;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;
import zypt.zyptapiserver.domain.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FocusTimeJpaRepository implements FocusTimeRepository{

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    private final QFocusTime focusTime = QFocusTime.focusTime1;
    private final QFragmentedUnfocusedTime fragmentedUnfocusedTime = QFragmentedUnfocusedTime.fragmentedUnfocusedTime;
    private final FocusTimeJdbcRepository focusTimeJdbcRepository;


    // focus time 영속성화
    @Transactional
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime startAt, LocalTime endAt, Long sumUnFocusedTimes) {
        FocusTime newFocusTime
                = new FocusTime(member, startAt, endAt, date, sumUnFocusedTimes);

        em.persist(newFocusTime);

        // 연관관계 설정
        member.addFocusTimes(newFocusTime);
        return Optional.of(newFocusTime);
    }


    @Override
    public void bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {
        focusTimeJdbcRepository.bulkInsertUnfocusedTimes(focusId, unfocusedTimes);
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        List<FocusTimeResponseDto> focusTimeResponseDtoList = queryFactory.select(new QFocusTimeResponseDto(
                        focusTime.id,
                        focusTime.member.id,
                        focusTime.startAt,
                        focusTime.endAt,
                        focusTime.createDate
                ))
                .from(focusTime)
                .where(focusTime.member.id.eq(memberId))
                .orderBy(focusTime.createDate.desc()) // 최신순
                .fetch();

        List<Long> ids = focusTimeResponseDtoList.stream().map(FocusTimeResponseDto::getId).toList();

        List<FragmentedUnFocusedTimeDto> fetch1 = queryFactory.select(Projections.constructor(
                        FragmentedUnFocusedTimeDto.class,
                        fragmentedUnfocusedTime.id,
                        fragmentedUnfocusedTime.focusTime.id,
                        fragmentedUnfocusedTime.startAt,
                        fragmentedUnfocusedTime.endAt,
                        fragmentedUnfocusedTime.type,
                        fragmentedUnfocusedTime.unfocusedTime
                ))
                .from(fragmentedUnfocusedTime)
                .where(fragmentedUnfocusedTime.focusTime.id.in(ids))
                .fetch();


        // 연관관계 설정
        Map<Long, List<FragmentedUnFocusedTimeDto>> dtos
                = fetch1.stream().collect(Collectors.groupingBy(FragmentedUnFocusedTimeDto::focusId));

        for (FocusTimeResponseDto focusTimeResponseDto : focusTimeResponseDtoList) {
            Long id = focusTimeResponseDto.getId();
            List<FragmentedUnFocusedTimeDto> unFocusedTimeDtos = dtos.get(id);
            focusTimeResponseDto.addUnFocusedTimeDtos(unFocusedTimeDtos);

        }


//        List<Tuple> result = queryFactory.select(focusTime, unfocusedTime)
//                .from(focusTime)
//                .leftJoin(unfocusedTime)
//                .on(focusTime.id.eq(unfocusedTime.focusTime.id))
//                .where(focusTime.member.id.eq(memberId))
//                .fetch();


        return focusTimeResponseDtoList;
    }

    @Override
    public List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIds) {
        return queryFactory.select(Projections.constructor(
                FragmentedUnFocusedTimeDto.class,
                fragmentedUnfocusedTime.id,
                fragmentedUnfocusedTime.focusTime.id,
                fragmentedUnfocusedTime.startAt,
                fragmentedUnfocusedTime.endAt,
                fragmentedUnfocusedTime.type,
                fragmentedUnfocusedTime.unfocusedTime
        )).from(fragmentedUnfocusedTime)
                .where(fragmentedUnfocusedTime.focusTime.id.in(focusIds))
                .fetch();
    }

    @Override
    public Optional<FocusTimeResponseDto> findFocusTime(long focusId) {
        FocusTimeResponseDto fetchOne = queryFactory.select(new QFocusTimeResponseDto(
                        focusTime.id,
                        focusTime.member.id,
                        focusTime.startAt,
                        focusTime.endAt,
                        focusTime.createDate
                ))
                .from(focusTime)
                .where(focusTime.id.eq(focusId))
                .fetchOne();

        if (fetchOne == null) {
            throw new FocusTimeNotFoundException("실패 id = " + focusId);
        }

        List<FragmentedUnFocusedTimeDto> unFocusedTimeDtos = queryFactory.select(Projections.constructor(
                        FragmentedUnFocusedTimeDto.class,
                        fragmentedUnfocusedTime.id,
                        fragmentedUnfocusedTime.focusTime.id,
                        fragmentedUnfocusedTime.startAt,
                        fragmentedUnfocusedTime.endAt,
                        fragmentedUnfocusedTime.type,
                        fragmentedUnfocusedTime.unfocusedTime
                ))
                .from(fragmentedUnfocusedTime)
                .where(fragmentedUnfocusedTime.focusTime.id.eq(focusId))
                .fetch();

        fetchOne.addUnFocusedTimeDtos(unFocusedTimeDtos);

        return Optional.of(fetchOne);
    }

    // unfocus는 서비스 단에서 처리
    @Override
    public List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {
        List<FocusTimeResponseDto> focusTimeResponseDtoList = queryFactory.select(new QFocusTimeResponseDto(
                        focusTime.id,
                        focusTime.member.id,
                        focusTime.startAt,
                        focusTime.endAt,
                        focusTime.createDate
                ))
                .from(focusTime)
                .where(focusTime.member.id.eq(memberId),
                        eqOrNull(focusTime.createDate.year(), year),
                        eqOrNull(focusTime.createDate.month(), month),
                        eqOrNull(focusTime.createDate.dayOfMonth(), day))
                .orderBy(focusTime.createDate.asc())
                .fetch();

        return focusTimeResponseDtoList;
    }

    @Override
    public List<Integer> findFocusTimesByMonth(String memberId, int year, int month) {
        return List.of();
    }


    // 해당 기간에 있는 기록들 조회 (통계용)
    public List<FocusTimeForStatisticsDto> findFocusTimeForStatistics(String memberId, LocalDate startDate, LocalDate endDate) {
        return queryFactory.select(new QFocusTimeForStatisticsDto(
                        focusTime.id,
                        focusTime.startAt,
                        focusTime.endAt,
                        focusTime.createDate
                ))
                .from(focusTime)
                .where(focusTime.member.id.eq(memberId)
                        .and(focusTime.createDate.between(startDate, endDate)))
                .orderBy(focusTime.createDate.desc())
                .fetch();
    }

    // 1분 이상의 집중하지않은 모든 시간 조회
    public List<UnFocusTimeForStatisticsDto> findUnFocusTimeForStatistics(List<Long> ids) {
        return queryFactory.select(new QUnFocusTimeForStatisticsDto(
                        fragmentedUnfocusedTime.startAt,
                        fragmentedUnfocusedTime.endAt,
                        fragmentedUnfocusedTime.unfocusedTime
                ))
                .from(fragmentedUnfocusedTime)
                .where(fragmentedUnfocusedTime.focusTime.id.in(ids)
                        .and(fragmentedUnfocusedTime.unfocusedTime.goe(60)))
                .fetch();
    }


    @Override
    @Transactional
    public void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day, List<Long> ids) {
        queryFactory.delete(focusTime)
                .where(focusTime.member.id.eq(memberId),
                        eqOrNull(focusTime.createDate.year(), year),
                        eqOrNull(focusTime.createDate.month(), month),
                        eqOrNull(focusTime.createDate.dayOfMonth(), day))
                .execute();

        queryFactory.delete(fragmentedUnfocusedTime)
                .where(fragmentedUnfocusedTime.focusTime.id.in(ids));
    }


    /**
     * 삭제할 focusTime의 id들을 모두 조회
     */
    @Override
    public List<Long> findFocusTimeIdsByDate(String memberId, Integer year, Integer month, Integer day) {
        return queryFactory.select(focusTime.id)
                .from(focusTime)
                .where(eqOrNull(focusTime.createDate.year(), year),
                        eqOrNull(focusTime.createDate.month(), month),
                        eqOrNull(focusTime.createDate.dayOfMonth(), day))
                .fetch();
    }

    public static <T> BooleanExpression eqOrNull(SimpleExpression<T> path, T value) {
        return value != null ? path.eq(value) : null;
    }


}
