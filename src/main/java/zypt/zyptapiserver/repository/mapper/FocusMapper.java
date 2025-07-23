package zypt.zyptapiserver.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.dto.FocusTimeResponseDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;

import java.util.List;

@Mapper
public interface FocusMapper {
    long saveFocusTime(FocusTime focusTime);

    void bulkInsert(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes);

    List<FocusTimeResponseDto> findAll(String memberId);

    FocusTimeResponseDto find(Long focusId);

    List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIds);

    List<FocusTimeResponseDto> findFocusTimeByDate(String memberId, Integer year, Integer month, Integer day);

    int deleteFocusTimeByDate(String memberId, Integer year, Integer month, Integer day);

    List<Long> findFocusTimeIdsByDate(String memberId, Integer year, Integer month, Integer day);
}



