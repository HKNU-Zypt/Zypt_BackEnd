package zypt.zyptapiserver.domain.dto;

import zypt.zyptapiserver.domain.FragmentedUnfocusedTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 클라이언트에게 전송할 DTO
public record FocusTimeResponseDto(Long id, String memberId, LocalTime startAt, LocalTime endAt, LocalDate createDate,
                                   List<FragmentedUnFocusedTimeDto> unFocusedTimeDtos) {

}
