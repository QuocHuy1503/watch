package com.example.watch.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Một component đơn giản để giữ danh sách các JWT đã bị thu hồi (blacklist).
 * Ở đây chúng ta dùng ConcurrentHashMap.newKeySet() để đảm bảo thread-safe.
 */
@Component
public class TokenBlacklist {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Đánh dấu token là đã bị thu hồi.
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * Kiểm tra xem token có nằm trong blacklist hay không.
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
