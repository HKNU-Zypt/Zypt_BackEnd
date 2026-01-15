package zypt.zyptapiserver.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import zypt.zyptapiserver.domain.enums.ErrorCode;
import zypt.zyptapiserver.dto.ApiErrorResponse;
import zypt.zyptapiserver.util.MDCUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper mapper = new ObjectMapper();
    /**
     * 서블릿 필터 단에서 인증 실패시 예외 반환
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("인증 불가 url = {}", request.getRequestURI());

        String detail = "해당 요청에 충분한 인증이 되지 않았습니다.";

        ApiErrorResponse apiErrorResponse = ErrorCode
                .UNAUTHORIZED_ERROR
                .getApiErrorResponse(detail);

        String body = mapper.writeValueAsString(apiErrorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("X-Request-ID", MDCUtils.getOrGenerateRequestId());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);

        MDCUtils.clear();
    }
}
