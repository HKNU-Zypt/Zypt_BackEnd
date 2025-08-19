package zypt.zyptapiserver.service.focustime;

import lombok.RequiredArgsConstructor;

import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsResponseDto;
import zypt.zyptapiserver.domain.dto.focustime.UnFocusTimeForStatisticsDto;
import zypt.zyptapiserver.repository.focustime.FocusTimeStatisticRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class FocusTimeStatisticsServiceImpl implements FocusTimeStatisticsService {

    private final FocusTimeStatisticRepository focusTimeStatisticRepository;

    @Override
    public FocusTimeForStatisticsResponseDto findFocusTimesForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate) {
        int[] focusScoresPerHours = new int[24];

        List<FocusTimeForStatisticsDto> timeForStatistics = focusTimeStatisticRepository.findFocusTimeForStatistics(memberId, startDate, endDate);
        List<Long> ids = timeForStatistics.stream()
                .mapToLong(FocusTimeForStatisticsDto::getId)
                .boxed()
                .toList();

        List<UnFocusTimeForStatisticsDto> unFocusTimeForStatistics = focusTimeStatisticRepository.findUnFocusTimeForStatistics(ids);

        // 집중한 시간 저장
        for (int i = 0; i < timeForStatistics.size(); i++) {
            FocusTimeForStatisticsDto statisticsDto = timeForStatistics.get(i);
            LocalTime startTime = statisticsDto.getStartAt();
            LocalTime endTime = statisticsDto.getEndAt();

            int start = startTime.getHour();
            int end = endTime.getHour();

            // end 시간이 00시를 넘어가는 경우 (내일로 넘어감)
            if (start > end) {

                for (int j = start + 1; j < end + 24; j++) {
                    focusScoresPerHours[(j) % 24] += 60;
                }

            } else {
                for (int j = start + 1; j < end; j++) {
                    focusScoresPerHours[j] += 60;
                }

            }

            // 시작과 끝 시간이 같다면
            if (start == end) {
                focusScoresPerHours[start] += (endTime.getMinute() - startTime.getMinute() + 1);

            } else {
                focusScoresPerHours[start] += 60 - startTime.getMinute();
                focusScoresPerHours[end] += endTime.getMinute();
            }

        }


        // 집중하지 않은 시간 빼기
        for (int i = 0; i < unFocusTimeForStatistics.size(); i++) {
            UnFocusTimeForStatisticsDto unFocusTimeForStatisticsDto = unFocusTimeForStatistics.get(i);
            LocalTime startTime = unFocusTimeForStatisticsDto.getStartAt();
            LocalTime endTime = unFocusTimeForStatisticsDto.getEndAt();

            int start = startTime.getHour();
            int end = endTime.getHour();

            // end 시간이 00시를 넘어가는 경우 (내일로 넘어감)
            if (start > end) {

                for (int j = start + 1; j < end + 24; j++) {
                    focusScoresPerHours[(j) % 24] -= 60;
                }

            } else {
                for (int j = start + 1; j < end; j++) {
                    focusScoresPerHours[j] -= 60;
                }

            }

            // 시작과 끝 시간이 같다면
            if (start == end) {
                focusScoresPerHours[start] -= (endTime.getMinute() - startTime.getMinute() + 1);

            } else {
                focusScoresPerHours[start] -= 60 - startTime.getMinute();
                focusScoresPerHours[end] -= endTime.getMinute();
            }

        }


        return new FocusTimeForStatisticsResponseDto(startDate, endDate, focusScoresPerHours);

    }

    private static boolean validateYearMonthDay(Integer year, Integer month, Integer day) {
        return year == null && month != null
                || year == null && day != null
                || day != null && month == null;
    }
}
