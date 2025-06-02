package com.example.watch.security;

import com.example.watch.entity.User;
import com.example.watch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * MyUserDetailsService implement UserDetailsService để Spring Security
 * có thể load UserDetails dựa trên email (username).
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Tìm User trong database theo email. Nếu không tìm thấy, ném UsernameNotFoundException.
     * Nếu tìm thấy, trả về một instance của org.springframework.security.core.userdetails.User
     * với username = email, password = passwordHash, và authority dựa trên role.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        User user = userOpt.get();

        // Giả sử user.getRole() trả về ví dụ "customer", "admin", v.v.
        // Chúng ta có thể đưa thẳng vào SimpleGrantedAuthority.
        // Nếu bạn muốn prefix "ROLE_", thì thay thế bên dưới thành:
        // List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(user.getRole())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}
