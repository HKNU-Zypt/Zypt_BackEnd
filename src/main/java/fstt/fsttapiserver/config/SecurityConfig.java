package fstt.fsttapiserver.config;

import fstt.fsttapiserver.auth.filter.JwtAuthenticationFilter;
import fstt.fsttapiserver.auth.service.AuthService;
import fstt.fsttapiserver.repository.MemberRepository;
import fstt.fsttapiserver.repository.RedisRepository;
import fstt.fsttapiserver.util.CookieUtils;
import fstt.fsttapiserver.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .headers(c ->
                        c.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable).disable()) // x Frame-Option 비활성화
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // 세션 사용 X
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/", "/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, authService)
                        , UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build();

    }

}
