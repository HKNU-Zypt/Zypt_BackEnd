package fstt.fsttapiserver.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Date;

@Slf4j
@Getter
@ConfigurationProperties("jwt")
public class JwtUtils {
    private String SECRET_KEY;
    private long ACCESS_TOKEN_EXPIRATION;
    private long REFRESH_TOKEN_EXPIRATION;
    private Key key;

    private JwtUtils(String SECRET_KEY, long ACCESS_TOKEN_EXPIRATION, long REFRESH_TOKEN_EXPIRATION) {
        this.SECRET_KEY = SECRET_KEY;
        this.ACCESS_TOKEN_EXPIRATION = ACCESS_TOKEN_EXPIRATION;
        this.REFRESH_TOKEN_EXPIRATION = REFRESH_TOKEN_EXPIRATION;
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    // 액세스 토큰 생성
    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer("FSTT")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();

    }

    // token에서 userPk 추출
    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 검증
    public boolean validationToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("token valid fail = {}", e.getMessage());
            return false;
        }
    }


//    public Authentication getAuthentication(String token) {
//        Claims claims = parseClaims(token);
//        String userId = claims.getSubject();
//
//
//    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
