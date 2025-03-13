package com.dodo.spring_chat_dodo.global.util.jwt;

import com.dodo.spring_chat_dodo.global.auth.constant.JwtRule;
import com.dodo.spring_chat_dodo.global.auth.constant.TokenStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

/**
 * JWT 활용에 필요한 메서드 모음
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    // 검사할 token 과 시크릿키를 받아 유효기간 & 유효 여부 판단.
    public TokenStatus getTokenStatus(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.error("INVALID_EXPIRED_JWT");
            return TokenStatus.EXPIRED; // 유효 기간 X
        } catch (JwtException e) {
            log.error("INVALID_JWT");
            // todo 나중에 비즈니스 로직용 예외처리
            throw new RuntimeException("INVALID_JWT Exception"); // 유효하지 않음
        }
    }

    // 쿠키에서 원하는 토큰을 찾기 (어세스 or 리프레시 토큰 찾기)
    public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenPrefix) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }

    // 특정 토큰의 시크릿 키를 가져오기
    public Key getSigningKey(String secretKey) {
        String encodedKey = encodeToBase64(secretKey);
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }

    private String encodeToBase64(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 리셋
    public Cookie resetToken(JwtRule tokenPrefix) {
        Cookie cookie = new Cookie(tokenPrefix.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
