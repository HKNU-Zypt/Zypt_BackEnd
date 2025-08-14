package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsResponseDto;
import zypt.zyptapiserver.service.focustime.FocusTimeService;
import zypt.zyptapiserver.service.focustime.FocusTimeStatisticsService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeDto;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeResponseDto;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Tag(name = "FocusTime API", description = "FocusTime API")
@RequestMapping("/api/focus_times")
@RestController
@RequiredArgsConstructor
public class FocusTimeController {


    private final FocusTimeService focusTimeService;
    private final FocusTimeStatisticsService statisticsService;

    @PostMapping
    @Operation(summary = "focusTime 저장", description = "액세스토큰 해더 필수, \n\n focusTimeInsertDto는 작성할 필요 없음, \n\n startAt, endAt의 경우 \"HH-mm-ss\" 형태로 보내면 됨")
    public ResponseEntity<?> saveFocusTime(@AuthenticationPrincipal CustomUserDetails details,
                                           @Valid @RequestBody FocusTimeDto focusTimeDto) {

        focusTimeService.saveFocusTime(details.getUsername(), focusTimeDto);
        return ResponseEntity.ok("저장 성공");
    }

    @GetMapping
    @Operation(summary = "focusTime 년-월-일 동적 조회", description = "액세스토큰 해더 필수, \n\n 연-월-일 조건 부여하여 조회, 월만 조회할 수 없고 항상 연-월 이렇게 순서대로 선행 조건이 있어야 후행조건 사용 가능")
    public ResponseEntity<?> findFocusTime(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam(value = "year", required = false)  Integer year,
                                           @RequestParam(value = "month", required = false) Integer month,
                                           @RequestParam(value = "day", required = false) Integer day) {

        List<FocusTimeResponseDto> ResponseDtos = focusTimeService.findFocusTimesByYearAndMonthAndDay(userDetails.getUsername(), year, month, day);
        return ResponseEntity.ok(ResponseDtos);
    }

    @GetMapping("/statistics")
    @Operation(summary = "focusTime 기간 통계정보 조회", description = "액세스토큰 해더 필수, \n\n yyyy-mm-dd, yyyy-mm-dd로 문자열로 2개의 기간을 파라미터로 전송")
    public ResponseEntity<FocusTimeForStatisticsResponseDto> findFocusTimeStatisticsInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                         @RequestParam(value = "from") LocalDate from,
                                                                                         @RequestParam(value = "to") LocalDate to) {
        FocusTimeForStatisticsResponseDto statisticsByDateRange = statisticsService.findFocusTimesForStatisticsByDateRange(userDetails.getUsername(), from, to);
        return ResponseEntity.ok(statisticsByDateRange);
    }

    @GetMapping("/all")
    @Operation(summary = "focusTime 전체 조회", description = "액세스토큰 해더 필수, \n\n 멤버의 모든 집중 데이터 조회")
    public ResponseEntity<?> findAllFocusTime(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FocusTimeResponseDto> allFocusTimes = focusTimeService.findAllFocusTimes(userDetails.getUsername());

        return ResponseEntity.ok(allFocusTimes);
    }

    @DeleteMapping
    @Operation(summary = "focusTime 년-월-일 동적 삭제", description = "액세스토큰 해더 필수, \n\n 연-월-일 조건 부여하여 삭제, 이것 또한 연-월-일 선행순서에 맞춰서 조건 걸어야함")
    public ResponseEntity<?> deleteFocusTime(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam(value = "year", required = false)  Integer year,
                                           @RequestParam(value = "month", required = false) Integer month,
                                           @RequestParam(value = "day", required = false) Integer day) {

        focusTimeService.deleteFocusTimeByYearAndMonthAndDay(userDetails.getUsername(), year, month, day);
        return ResponseEntity.ok("집중 데이터 삭제 완료");
    }





}
