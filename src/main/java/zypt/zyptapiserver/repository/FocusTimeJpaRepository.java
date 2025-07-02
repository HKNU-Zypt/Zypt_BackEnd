package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FocusTimeJpaRepository implements FocusTimeRepository{

    @PersistenceContext
    private final EntityManager em;


    // focus time 영속성화
    @Transactional
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime start_at, LocalTime end_at) {
        FocusTime newFocusTime
                = new FocusTime(member, start_at, end_at, date);

        em.persist(newFocusTime);
        return Optional.of(newFocusTime);
    }


    @Override
    public Long bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {

        return null;
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        return List.of();
    }

    @Override
    public List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIdList) {
        return List.of();
    }

    @Override
    public Optional<FocusTimeResponseDto> findFocusTime(long focusId) {
        return Optional.empty();
    }

    @Override
    public List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {
        return List.of();
    }

    @Override
    public List<Integer> findFocusTimesByMonth(String memberId, int year, int month) {
        return List.of();
    }

    @Override
    public void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {

    }


}
