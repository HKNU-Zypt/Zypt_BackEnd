package zypt.zyptapiserver.config;

import zypt.zyptapiserver.auth.filter.CustomAccessDeniedHandler;
import zypt.zyptapiserver.auth.filter.CustomAuthenticationEntryPoint;
import zypt.zyptapiserver.auth.filter.JwtAuthenticationFilter;
import zypt.zyptapiserver.auth.service.AuthService;
import zypt.zyptapiserver.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final AuthService authService;

    // 스프링 애플리케이션 부팅시 호출됨
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 비활성화
                .logout(AbstractHttpConfigurer::disable) // 기본 로그아웃 비활성화
                .csrf(AbstractHttpConfigurer::disable) // 쿠키를 사용하지 않을 것이므로 CSRF 비활성화
                .headers(c ->
                        c.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable).disable()) // x Frame-Option 비활성화
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // 세션 사용 X
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/login/**",
                                        "/api/auth/logout",
                                        "/api/auth/refresh",
                                        "/v3/api-docs/**", // Swagger API 문서 정의 허용 (OpenAPI 3)
                                        "/favicon.ico",
                                        "/swagger-ui/**"  // Swagger UI 정적 리소스 허용, 실 운영 서버에선 제거
                                ).permitAll()
                                // 관리자 권한
                                .requestMatchers(
                                        "/api/admin/**"
                                ).hasRole("ADMIN")
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().permitAll()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, authService)
                        , UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build();

    }

}
