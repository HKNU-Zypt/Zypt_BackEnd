package zypt.zyptapiserver.auth.filter;

import com.fasterxml.uuid.Generators;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import zypt.zyptapiserver.util.MDCUtils;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 서블릿 필터 단에서 인증 실패시 예외 반환
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("인증 불가 url = {}", request.getRequestURI());

        String responseBody = "해당 요청에 충분한 인증이 되지 않았습니다.";

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("X-Request-ID", MDCUtils.getOrGenerateRequestId());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);

        MDCUtils.clear();
    }
}
