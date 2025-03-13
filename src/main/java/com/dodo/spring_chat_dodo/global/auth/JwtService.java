package com.dodo.spring_chat_dodo.global.auth;

import com.dodo.spring_chat_dodo.global.auth.constant.JwtRule;
import com.dodo.spring_chat_dodo.global.auth.constant.Role;
import com.dodo.spring_chat_dodo.global.auth.constant.TokenStatus;
import com.dodo.spring_chat_dodo.global.util.jwt.JwtGenerator;
import com.dodo.spring_chat_dodo.global.util.jwt.JwtUtil;
import com.dodo.spring_chat_dodo.user.entity.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;

import static com.dodo.spring_chat_dodo.global.auth.constant.JwtRule.ACCESS_PREFIX;
import static com.dodo.spring_chat_dodo.global.auth.constant.JwtRule.REFRESH_PREFIX;

/**
 * Jwt 핵심 로직
 */

@Service
@Transactional(readOnly = true)
@Slf4j
public class JwtService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
//    private final TokenRepository tokenRepository;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtService(CustomUserDetailsService customUserDetailsService,
                      JwtGenerator jwtGenerator,
                      JwtUtil jwtUtil,
                      @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
                      @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
                      @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
                      @Value("${jwt.refresh-expiration}")long REFRESH_EXPIRATION) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }

    public void validateUser(User requestUser) {
        if (requestUser.getRole() == Role.NOT_REGISTERED) {
            throw new RuntimeException("NOT_AUTHENTICATED_USER");
        }
    }

    // access token 생성
    @Transactional
    public String generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JwtRule.JWT_ISSUE_HEADER.getValue(), cookie.toString());

        return accessToken;
    }

    // refresh token 생성
    @Transactional
    public String generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie =setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JwtRule.JWT_ISSUE_HEADER.getValue(), cookie.toString());

        // todo 토큰 레포지토리에 저장하는 로직 만들기
//        tokenRepository.save(new Token(requestUser.getIdentifier(), refreshToken));
        return refreshToken;
    }

    // 토큰을 쿠키에 세팅
    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAge) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
    }



    // 쿠키에서 원하는 토큰 추출
    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("JWT_TOKEN_NOT_FOUND"); // todo 비즈니스 로직 예외처리
        }
        return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
    }

    // 전달받은 토큰이 유효한지 검사
    public boolean validateAccessToken(String token) {
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token, String id) {
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;

//        Token storedToken = tokenRepository.findById(id);
//        boolean isTokenMatched = storedToken.getToken().equals(token);

//        return isRefreshValid && isTokenMatched;
        // todo 리프레시토큰 저장소 검사 로직 추가
        return isRefreshValid;
    }

    // 시큐리티 컨텍스트홀더에 저장할 authentication 객체 생성 -> 인증필터에 사용할 예정
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getUserId(token, ACCESS_SECRET_KEY));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUserId(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getIdFromAccess(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(ACCESS_SECRET_KEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid Access Token");
        }
    }

    public String getIdFromRefresh(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }

    // todo : Refresh Token 저장소 구현
    public void logout(User requestUser, HttpServletResponse response) {
//        tokenRepository.deleteById(requestUser.getId());

        Cookie accessCookie = jwtUtil.resetToken(ACCESS_PREFIX);
        Cookie refreshCookie = jwtUtil.resetToken(REFRESH_PREFIX);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
