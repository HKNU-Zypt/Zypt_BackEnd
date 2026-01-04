package zypt.zyptapiserver.service.focustime;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.dto.focustime.FocusTimeForStatisticsDto;
import zypt.zyptapiserver.dto.focustime.FocusTimeForStatisticsResponseDto;
import zypt.zyptapiserver.dto.focustime.UnFocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.enums.UnFocusedType;
import zypt.zyptapiserver.repository.focustime.FocusTimeStatisticRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FocusTimeStatisticsServiceImpl implements FocusTimeStatisticsService {

    private final FocusTimeStatisticRepository focusTimeStatisticRepository;

    @Override
    public FocusTimeForStatisticsResponseDto findFocusTimesForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate) {
        log.info("{} ~ {} focusTime 분석 데이터 조회", startDate, endDate);

        int[] focusScoresPerHours = new int[24];

        List<FocusTimeForStatisticsDto> timeForStatistics = focusTimeStatisticRepository.findFocusTimeForStatistics(memberId, startDate, endDate);
        List<Long> ids = timeForStatistics.stream()
                .mapToLong(FocusTimeForStatisticsDto::getId)
                .boxed()
                .toList();

        // 비집중 데이터 조회
        List<UnFocusTimeForStatisticsDto> unFocusTimeForStatistics = focusTimeStatisticRepository.findUnFocusTimeForStatistics(ids, null);

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
                int time = endTime.getMinute() - startTime.getMinute();
                focusScoresPerHours[start] -= time;

            } else {
                focusScoresPerHours[start] -= 60 - startTime.getMinute();
                focusScoresPerHours[end] -= endTime.getMinute();
            }

        }

        // -시간 방지
        for (int i = 0; i < 24; i++) {
            if (focusScoresPerHours[i] < 0) {
                focusScoresPerHours[i] = 0;
            }
        }

        return new FocusTimeForStatisticsResponseDto(startDate, endDate, focusScoresPerHours);

    }

    @Override
    public FocusTimeForStatisticsResponseDto findSleepCountForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate) {
        log.info("{} ~ {} Sleep 카운트 통계 데이터 조회", startDate, endDate);

        int[] sleepCountPerHours = new int[24];

        List<FocusTimeForStatisticsDto> timeForStatistics = focusTimeStatisticRepository.findFocusTimeForStatistics(memberId, startDate, endDate);
        List<Long> ids = timeForStatistics.stream()
                .mapToLong(FocusTimeForStatisticsDto::getId)
                .boxed()
                .toList();

        // 비집중 데이터 조회
        List<UnFocusTimeForStatisticsDto> unFocusTimeForStatistics = focusTimeStatisticRepository.findUnFocusTimeForStatistics(ids, UnFocusedType.SLEEP);

        for (int i = 0; i < unFocusTimeForStatistics.size(); i++) {

            UnFocusTimeForStatisticsDto unFocusTimeForStatisticsDto = unFocusTimeForStatistics.get(i);
            LocalTime startAt = unFocusTimeForStatisticsDto.getStartAt();
            LocalTime endAt = unFocusTimeForStatisticsDto.getEndAt();

            int start = startAt.getHour();
            int end = endAt.getHour();

            if (start > end) {
                log.info("{} {}", start, end + 24);
                // 잠든 각 시각대에 카운팅
                for (int hour = start; hour <= end + 24; hour++) {
                    sleepCountPerHours[(hour) % 24]++;
                }
            } else {
                for (int hour = start; hour <= end; hour++) {
                    sleepCountPerHours[(hour) % 24]++;
                }
            }
        }


        return new FocusTimeForStatisticsResponseDto(startDate, endDate, sleepCountPerHours);
    }

    @Override
    public FocusTimeForStatisticsResponseDto findDistractedCountForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate) {
        log.info("{} ~ {} Distracted 카운트 통계 데이터 조회", startDate, endDate);

        int[] distractedCountPerHours = new int[24];

        List<FocusTimeForStatisticsDto> timeForStatistics = focusTimeStatisticRepository.findFocusTimeForStatistics(memberId, startDate, endDate);
        List<Long> ids = timeForStatistics.stream()
                .mapToLong(FocusTimeForStatisticsDto::getId)
                .boxed()
                .toList();

        // 비집중 데이터 조회
        List<UnFocusTimeForStatisticsDto> unFocusTimeForStatistics = focusTimeStatisticRepository.findUnFocusTimeForStatistics(ids, UnFocusedType.DISTRACTED);

        for (int i = 0; i < unFocusTimeForStatistics.size(); i++) {

            UnFocusTimeForStatisticsDto unFocusTimeForStatisticsDto = unFocusTimeForStatistics.get(i);
            LocalTime startAt = unFocusTimeForStatisticsDto.getStartAt();
            LocalTime endAt = unFocusTimeForStatisticsDto.getEndAt();

            int start = startAt.getHour();
            int end = endAt.getHour();

            if (start > end) {
                // 잠든 각 시각대에 카운팅
                for (int hour = start; hour <= end + 24; hour++) {
                    distractedCountPerHours[(hour) % 24]++;
                }
            } else {
                for (int hour = start; hour <= end; hour++) {
                    distractedCountPerHours[(hour) % 24]++;
                }
            }
        }
        return new FocusTimeForStatisticsResponseDto(startDate, endDate, distractedCountPerHours);
    }

    private static boolean validateYearMonthDay(Integer year, Integer month, Integer day) {
        return year == null && month != null
                || year == null && day != null
                || day != null && month == null;
    }
}
