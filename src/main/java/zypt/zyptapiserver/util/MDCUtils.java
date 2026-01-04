package zypt.zyptapiserver.util;

import com.fasterxml.uuid.Generators;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public class MDCUtils {

    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_URI = "requestURI";

    public static String getOrGenerateRequestId() {
        String requestId = MDC.get(REQUEST_ID);
        if (requestId == null || requestId.isEmpty()) {
            requestId = Generators.timeBasedEpochRandomGenerator().generate().toString();
            MDC.put(REQUEST_ID, requestId);
        }
        return requestId;
    }

    public static String getOrGenerateRequestUri(HttpServletRequest request) {
        String requestUri = MDC.get(REQUEST_URI);
        if (requestUri == null || requestUri.isEmpty()) {
            requestUri = request.getRequestURI();
            MDC.put(REQUEST_URI, requestUri);
        }
        return requestUri;
    }

    public static void clear() {
        MDC.clear();
    }
}
