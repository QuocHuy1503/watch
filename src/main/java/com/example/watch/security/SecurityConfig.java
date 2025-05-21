package com.example.watch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Mở public cho swagger & openapi
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // Mở public cho các API công khai (nếu có)
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/**"
                        ).permitAll()
                        // Còn lại phải xác thực
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());  // Bật Basic Auth
        return http.build();
    }
}
