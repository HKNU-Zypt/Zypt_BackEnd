package zypt.zyptapiserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.FocusTimeResponseDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;
import zypt.zyptapiserver.repository.mapper.FocusMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FocusTimeMyBatisRepository implements FocusTimeRepository {

    private final FocusMapper focusMapper;

    @Override
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime startAt, LocalTime endAt, Long sumUnFocusedTimes) {
        long totalTime = ChronoUnit.SECONDS.between(startAt, endAt);
        FocusTime focusTime = new FocusTime(member, startAt, endAt, date, totalTime - sumUnFocusedTimes, totalTime, null);
        focusMapper.saveFocusTime(focusTime);

        return Optional.of(focusTime);
    }

    @Override
    public void bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {
        focusMapper.bulkInsert(focusId, unfocusedTimes);
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        return focusMapper.findAll(memberId);
    }

    @Override
    public List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIdList) {
        return focusMapper.findAllFragmentedUnFocusTimes(focusIdList);
    }

    @Override
    public Optional<FocusTimeResponseDto> findFocusTime(long focusId) {
        FocusTimeResponseDto focusTimeResponseDto = focusMapper.find(focusId);
        return Optional.ofNullable(focusTimeResponseDto);
    }

    @Override
    public List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {
        return focusMapper.findFocusTimeByDate(memberId, year, month, day);
    }

    @Override
    public List<Integer> findFocusTimesByMonth(String memberId, int year, int month) {
        return List.of();
    }

    @Override
    public void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day, List<Long> ids) {
        focusMapper.deleteFocusTimeByDate(memberId, year, month, day);
    }

    @Override
    public List<Long> findFocusTimeIdsByDate(String memberId, Integer year, Integer month, Integer day) {
        return focusMapper.findFocusTimeIdsByDate(memberId, year, month, day);
    }
}
