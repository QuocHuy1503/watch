package com.example.watch.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Cấu hình Spring Security (Spring Boot 3 / Spring Security 6.1+):
 *
 * 1) Tắt CSRF (vì dùng JWT, không cần session-based).
 * 2) Cho phép public truy cập Swagger/OpenAPI và /api/auth/{login,register,forgot-password,reset-password}.
 * 3) Mọi request khác yêu cầu JWT hợp lệ (chưa bị blacklist).
 * 4) Stateless session (JWT mang toàn bộ thông tin, không dùng session server-side).
 * 5) Đăng ký DaoAuthenticationProvider.
 * 6) Thêm JwtRequestFilter trước UsernamePasswordAuthenticationFilter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) Tắt CSRF vì không dùng session cookie
                .csrf(csrf -> csrf.disable())

                // 2) Quy định quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép public truy cập Swagger/OpenAPI
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // Cho phép public các endpoint /api/auth/login, /api/auth/register, /api/auth/forgot-password, /api/auth/reset-password
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()
                        // Tất cả request khác yêu cầu phải xác thực
                        .anyRequest().authenticated()
                )

                // 3) Stateless session — không lưu session server-side (dành cho JWT API)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4) Đăng ký DaoAuthenticationProvider (MyUserDetailsService + PasswordEncoder)
                .authenticationProvider(authenticationProvider)

                // 5) Kích hoạt OAuth2 login với trang /login mặc định của Spring.
                .oauth2Login(oauth2 -> oauth2
                        // spring sẽ tự xử lý endpoint /oauth2/authorization/{registrationId}
                        .defaultSuccessUrl("/oauth2/success", true)
                );

        // 6) Thêm JwtRequestFilter trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
