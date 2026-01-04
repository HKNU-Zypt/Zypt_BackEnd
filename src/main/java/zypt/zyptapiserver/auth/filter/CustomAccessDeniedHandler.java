package zypt.zyptapiserver.auth.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import zypt.zyptapiserver.util.MDCUtils;

import java.io.IOException;

// 인가는 되었으나 해당 등급에서는 접근 불가능할때 403 예외 잡기
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String responseBody = "해당 요청에 대한 권한이 없습니다.";

        log.info("권한 없음");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Request-ID", MDCUtils.getOrGenerateRequestId());
        response.getWriter().write(responseBody);


        MDCUtils.clear();
    }
}
