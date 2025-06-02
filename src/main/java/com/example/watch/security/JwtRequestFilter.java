package com.example.watch.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter để xử lý JWT trước khi request đi vào Controller.
 *
 * 1) Lấy token từ header Authorization.
 * 2) Gọi jwtUtil.extractEmail(...) để kiểm tra xem token đã bị blacklist (revoke) hay hết hạn chưa.
 * 3) Nếu hợp lệ, load UserDetails và gọi jwtUtil.validateToken(...) để xác thực tiếp.
 * 4) Nếu thành công, set Authentication vào SecurityContext.
 * 5) Nếu token đã hết hạn / bị revoke / không hợp lệ → Spring Security sẽ trả 401.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // 1. Lấy token từ header Authorization (nếu có)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                // 2. extractEmail sẽ kiểm tra blacklist; nếu token đã bị revoke → ném RuntimeException
                username = jwtUtil.extractEmail(jwtToken);
            } catch (ExpiredJwtException e) {
                // Token đã hết hạn → không set Authentication; để Spring Security trả 401
            } catch (RuntimeException e) {
                // RuntimeException("Token has been revoked") hoặc các lỗi do blacklist
                // → không set Authentication; để Spring Security trả 401
            } catch (Exception e) {
                // Mọi lỗi khác (token không hợp lệ, chữ ký sai, v.v.) → không set Authentication
            }
        }

        // 3. Nếu đã lấy được username và chưa có Authentication trong context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load UserDetails từ DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. Kiểm tra lại bằng validateToken (check expiration + blacklist nếu có)
            if (jwtUtil.validateToken(jwtToken)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Cho phép tiếp tục chain
        chain.doFilter(request, response);
    }
}
