package zypt.zyptapiserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import zypt.zyptapiserver.dto.ErrorReportDto;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final NamedParameterJdbcTemplate template;

    public void saveReport(ErrorReportDto reportDto) {
        String sql = "insert into error_report values(null, :member_id, :req_id, :report_date, :body)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("member_id", reportDto.memberId());
        param.addValue("req_id", reportDto.reqId());
        param.addValue("report_date", reportDto.date());
        param.addValue("body", reportDto.body());

        int update = template.update(sql, param);

        if (update == 0) {
            throw new IllegalArgumentException("에러 리포트 저장 실패, 잘못된 요청 값");
        }
    }


}
