package com.dodo.spring_chat_dodo.global.auth;

import com.dodo.spring_chat_dodo.global.auth.constant.JwtRule;
import com.dodo.spring_chat_dodo.user.entity.User;
import com.dodo.spring_chat_dodo.user.repository.UserRepository;
import com.dodo.spring_chat_dodo.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // 이 경로들은 JWT 필터를 거치지 않음
    private final List<String> excludedPaths = Arrays.asList(
            "/api/auth/",
            "/api/users",  // POST 메소드만 허용하려면 컨트롤러에서 처리
            "/ws/",
            "/api/test/"
            // 여기에 JWT 필터를 거치지 않을 경로 추가
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // jwt를 거칠 필요가 없는 열린 요청
        // 헤더에서 JWT 토큰 추출
        String authHeader = request.getHeader("Authorization");

        // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 진행
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);

        // 어세스 토큰 검사 통과 시 -> 다음 필터로
        if (jwtService.validateAccessToken(accessToken)) {
            setAuthenticationToContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);
        User user = findUserByRefreshToken(refreshToken);

        // 통과 못했으면 리프레시 토큰 검사
        if (jwtService.validateRefreshToken(refreshToken, String.valueOf(user.getId()))) {
            String reissuedAccessToken = jwtService.generateAccessToken(response, user);
            jwtService.generateRefreshToken(response, user);

            setAuthenticationToContext(reissuedAccessToken);
            filterChain.doFilter(request, response);
            return;
        }

        // 둘 다 통과 못했으면 로그아웃함
        jwtService.logout(user, response);
    }

    private User findUserByRefreshToken(String refreshToken) {
        String id = jwtService.getIdFromRefresh(refreshToken);
        return userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsernameNotFoundException("no user found with refresh token's id : " + id));
    }

    private void setAuthenticationToContext(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}