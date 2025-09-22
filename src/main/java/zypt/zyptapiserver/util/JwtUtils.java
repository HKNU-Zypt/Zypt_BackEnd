package zypt.zyptapiserver.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import zypt.zyptapiserver.exception.InvalidTokenException;

import java.security.Key;
import java.security.PublicKey;
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
                .setIssuer("Zypt")
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

    @Deprecated
    public Claims extractInfo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // token에서 userPk 추출
    public String extractId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractNickName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickName", String.class);
    }

    // 토큰 검증
    public boolean validationToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
            // 만료 일 시에만 false 반환
        } catch (ExpiredJwtException e) {
            log.warn("token expired = {}", e.getMessage());
            return false;

            // 그외에는 예외 던지기
        } catch (SignatureException e) {
            log.warn("token signature invalid ", e.getCause());
            return false;
        } catch (JwtException e) {
            log.warn("token is invalid", e.getCause());
            return false;
            // 토큰 누락
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return false;
        }

    }

    public Claims getSubjectEvenIfExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * id token 검증
     * 만료 여부까지 확인
     * @param idToken
     * @param iss
     * @param key
     */
    public Claims validationIdToken(String idToken, String aud, String iss, PublicKey key) {


        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(iss)
                    .requireAudience(aud)
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(idToken);

            Claims claims = jws.getBody();
            log.info("public key를 통한 OIDC 토큰 검증 성공");
            return claims;

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("만료된 토큰 : " + e);
        } catch (JwtException e) {
            throw new RuntimeException("검증 실패 :", e);
        }
    }
}
