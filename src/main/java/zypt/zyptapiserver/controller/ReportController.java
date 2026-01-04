package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zypt.zyptapiserver.dto.ErrorReportDto;
import zypt.zyptapiserver.repository.ReportRepository;

@RestController
@Tag(name = "Report API", description = "버그 제보")
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository repository;

    @PostMapping("/error")
    public ResponseEntity<String> errorReport(@RequestBody ErrorReportDto errorReportDto) {
        repository.saveReport(errorReportDto);

        return ResponseEntity.ok("제보 완료");
    }

}
