package fstt.fsttapiserver.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {

    public static void addCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken")
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("Strict")
                .value(refreshToken)
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
