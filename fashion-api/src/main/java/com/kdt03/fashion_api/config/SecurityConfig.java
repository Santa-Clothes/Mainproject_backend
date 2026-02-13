package com.kdt03.fashion_api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kdt03.fashion_api.config.filter.JWTAuthorizationFilter;
import com.kdt03.fashion_api.repository.MemberRepository;
import com.kdt03.fashion_api.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final MemberRepository memberRepo;
        private final JWTUtil jwtUtil;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                // CORS 설정
                http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

                // CSRF 비활성화
                http.csrf(csrf -> csrf.disable());

                // 세션 정책: STATELESS
                http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                // 모든 요청 허용 (오류 해결을 위한 전면 개방)
                http.authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll());

                // 기본 로그인 폼 및 HTTP Basic 비활성화
                http.formLogin(form -> form.disable());
                http.httpBasic(basic -> basic.disable());

                // JWT 인증 필터 추가 (인증 정보는 채우되 통과는 시킴)
                http.addFilterBefore(new JWTAuthorizationFilter(memberRepo, jwtUtil),
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(false); // CORS 오류 해결: wildcard origin과 credentials는 함께 사용 불가

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

}
