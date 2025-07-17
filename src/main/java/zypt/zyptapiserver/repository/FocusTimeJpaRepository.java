package zypt.zyptapiserver.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.auth.exception.FocusTimeNotFoundException;
import zypt.zyptapiserver.domain.*;
import zypt.zyptapiserver.domain.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FocusTimeJpaRepository implements FocusTimeRepository{

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    private final QFocusTime focusTime = QFocusTime.focusTime1;
    private final QFragmentedUnfocusedTime unfocusedTime = QFragmentedUnfocusedTime.fragmentedUnfocusedTime;

    private final FocusTimeJdbcRepository focusTimeJdbcRepository;

    // focus time 영속성화
    @Transactional
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime start_at, LocalTime end_at, Long sumUnFocusedTimes) {
        FocusTime newFocusTime
                = new FocusTime(member, start_at, end_at, date, sumUnFocusedTimes);

        em.persist(newFocusTime);

        // 연관관계 설정
        member.addFocusTimes(newFocusTime);
        return Optional.of(newFocusTime);
    }


    @Override
    public Long bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {
        return focusTimeJdbcRepository.bulkInsertUnfocusedTimes(focusId, unfocusedTimes);
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
                .fetch();

        List<Long> ids = focusTimeResponseDtoList.stream().map(FocusTimeResponseDto::getId).toList();

        List<FragmentedUnFocusedTimeDto> fetch1 = queryFactory.select(Projections.constructor(
                        FragmentedUnFocusedTimeDto.class,
                        unfocusedTime.id,
                        unfocusedTime.focusTime.id,
                        unfocusedTime.startAt,
                        unfocusedTime.endAt,
                        unfocusedTime.type,
                        unfocusedTime.unfocusedTime
                ))
                .from(unfocusedTime)
                .where(unfocusedTime.focusTime.id.in(ids))
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
    public List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIdList) {
        return queryFactory.select(Projections.constructor(
                FragmentedUnFocusedTimeDto.class,
                unfocusedTime.id,
                unfocusedTime.focusTime.id,
                unfocusedTime.startAt,
                unfocusedTime.endAt,
                unfocusedTime.type,
                unfocusedTime.unfocusedTime
        )).from(unfocusedTime)
                .where(unfocusedTime.focusTime.id.in(focusIdList))
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
                        unfocusedTime.id,
                        unfocusedTime.focusTime.id,
                        unfocusedTime.startAt,
                        unfocusedTime.endAt,
                        unfocusedTime.type,
                        unfocusedTime.unfocusedTime
                ))
                .from(unfocusedTime)
                .where(unfocusedTime.focusTime.id.eq(focusId))
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
                .fetch();

        return focusTimeResponseDtoList;
    }

    @Override
    public List<Integer> findFocusTimesByMonth(String memberId, int year, int month) {
        return List.of();
    }

    @Override
    public void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day, List<Long> ids) {
        queryFactory.delete(focusTime)
                .where(focusTime.member.id.eq(memberId),
                        eqOrNull(focusTime.createDate.year(), year),
                        eqOrNull(focusTime.createDate.month(), month),
                        eqOrNull(focusTime.createDate.dayOfMonth(), day))
                .execute();

        queryFactory.delete(unfocusedTime)
                .where(unfocusedTime.focusTime.id.in(ids));

    }

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
