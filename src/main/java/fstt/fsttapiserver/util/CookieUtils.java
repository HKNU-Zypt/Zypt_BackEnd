package fstt.fsttapiserver.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {

    public static void addCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken")
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("Strict") // 완전한 사이트 간 요청 방지. 외부 링크에서 쿠키 전송 안됨
                .value(refreshToken)
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
