package zypt.zyptapiserver.aop.intercepter;

import com.fasterxml.uuid.Generators;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 시작 시 requestId 생성 후 MDC에 저장
        String requestId = Generators.timeBasedEpochRandomGenerator().generate().toString();
        MDC.put("requestId", requestId);
        MDC.put("requestURI", request.getRequestURI());

        // 응답 헤더에도 넣어주면 클라이언트가 추적할 수 있음
        response.setHeader("X-Request-ID", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 요청 종료 시 MDC clear (메모리 누수 방지)
        MDC.clear();
    }
}
