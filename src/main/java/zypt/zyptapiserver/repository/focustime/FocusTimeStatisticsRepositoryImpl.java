package zypt.zyptapiserver.repository.focustime;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import zypt.zyptapiserver.domain.QFocusTime;
import zypt.zyptapiserver.domain.QFragmentedUnfocusedTime;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.dto.focustime.QFocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.dto.focustime.QUnFocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.dto.focustime.UnFocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FocusTimeStatisticsRepositoryImpl implements FocusTimeStatisticRepository {

    private final JPAQueryFactory queryFactory;

    private final QFocusTime focusTime = QFocusTime.focusTime1;
    private final QFragmentedUnfocusedTime fragmentedUnfocusedTime = QFragmentedUnfocusedTime.fragmentedUnfocusedTime;

    @Override
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



    @Override
    public List<UnFocusTimeForStatisticsDto> findUnFocusTimeForStatistics(List<Long> ids, UnFocusedType unFocusedType) {
        return queryFactory.select(new QUnFocusTimeForStatisticsDto(
                        fragmentedUnfocusedTime.startAt,
                        fragmentedUnfocusedTime.endAt,
                        fragmentedUnfocusedTime.unfocusedTime
                ))
                .from(fragmentedUnfocusedTime)
                .where(fragmentedUnfocusedTime.focusTime.id.in(ids),
                        unFocusedType != null
                                ? fragmentedUnfocusedTime.type.eq(unFocusedType) : null,
                        fragmentedUnfocusedTime.unfocusedTime.goe(60))
                .fetch();
    }
}
