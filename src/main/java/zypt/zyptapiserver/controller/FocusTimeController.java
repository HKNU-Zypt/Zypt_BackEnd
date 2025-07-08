package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.FocusTimeService;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.domain.dto.FocusTimeDto;
import zypt.zyptapiserver.domain.dto.FocusTimeResponseDto;

import java.util.List;

@Slf4j
@Tag(name = "FocusTime API", description = "FocusTime API")
@RequestMapping("/api/focus_times")
@RestController
@RequiredArgsConstructor
public class FocusTimeController {


    private final FocusTimeService focusTimeService;

    @PostMapping
    public ResponseEntity<?> saveFocusTime(@AuthenticationPrincipal CustomUserDetails details,
                                           @Valid @RequestBody FocusTimeDto focusTimeDto) {


        focusTimeService.saveFocusTime(details.getUsername(), focusTimeDto);
        return ResponseEntity.ok("저장 성공");
    }

    @GetMapping
    public ResponseEntity<?> findFocusTime(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam(value = "year", required = false)  Integer year,
                                           @RequestParam(value = "month", required = false) Integer month,
                                           @RequestParam(value = "day", required = false) Integer day) {


        List<FocusTimeResponseDto> ResponseDtos = focusTimeService.findFocusTimesByYearAndMonthAndDay(userDetails.getUsername(), year, month, day);

        return ResponseEntity.ok(ResponseDtos);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllFocusTime(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FocusTimeResponseDto> allFocusTimes = focusTimeService.findAllFocusTimes(userDetails.getUsername());

        return ResponseEntity.ok(allFocusTimes);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFocusTime(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam(value = "year", required = false)  Integer year,
                                           @RequestParam(value = "month", required = false) Integer month,
                                           @RequestParam(value = "day", required = false) Integer day) {



        focusTimeService.deleteFocusTimeByYearAndMonthAndDay(userDetails.getUsername(), year, month, day);
        return ResponseEntity.ok("집중 데이터 삭제 완료");
    }



}
