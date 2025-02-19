package com.newstickr.newstickr.security.config;

import com.newstickr.newstickr.security.jwt.JWTFilter;
import com.newstickr.newstickr.security.jwt.JWTUtil;
import com.newstickr.newstickr.security.oauth2.CustomSuccessHandler;
import com.newstickr.newstickr.user.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 로그인 및 인증 방식 비활성화
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // JWT 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );

        // 경로별 인가 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
        );

        // 세션 상태 설정 (STATELESS)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
