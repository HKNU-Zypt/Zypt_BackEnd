package zypt.zyptapiserver.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import zypt.zyptapiserver.util.MDCUtils;

import java.io.IOException;

public class MDCFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestId = MDCUtils.getOrGenerateRequestId();
            String requestUri = MDCUtils.getOrGenerateRequestUri(request);

            response.setHeader("X-Request-ID", requestId);
            filterChain.doFilter(request, response);

        } finally {
            MDCUtils.clear();
        }
    }
}
