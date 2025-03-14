package com.dodo.spring_chat_dodo.global.auth;

import com.dodo.spring_chat_dodo.global.auth.constant.JwtRule;
import com.dodo.spring_chat_dodo.global.auth.dto.LoginRequestDto;
import com.dodo.spring_chat_dodo.user.dto.UserResponseDto;
import com.dodo.spring_chat_dodo.user.entity.User;
import com.dodo.spring_chat_dodo.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // token 발급 요청
    @PostMapping("")
    public ResponseEntity<String> generateToken(HttpServletResponse response,
                                                @RequestBody String tokenRequestById) {
        User requestUser = userRepository.findById(Long.parseLong(tokenRequestById))
                .orElseThrow(() -> new UsernameNotFoundException("user not found : " + tokenRequestById));
        // 등록 회원인지 검증
        jwtService.validateUser(requestUser);
        // 토큰 발급
        jwtService.generateAccessToken(response,requestUser);
        jwtService.generateRefreshToken(response,requestUser);

        return ResponseEntity.ok("LOGIN");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Validated LoginRequestDto loginRequestDto,
                                                 HttpServletResponse response) {

        // 로그인 검증
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("user not found : " + loginRequestDto.getEmail()));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("password not match");
        }

        // 인증 성공 시, 토큰 발급하고 SecurityContext 에 정보 저장
        String accessToken = jwtService.generateAccessToken(response, user);
        jwtService.generateRefreshToken(response, user);
        SecurityContextHolder.getContext().setAuthentication(jwtService.getAuthentication(accessToken));

        return ResponseEntity.ok(
                UserResponseDto.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .profile(user.getProfile())
                        .createdAt(user.getCreatedAt())
                        .build()
        );
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        // 로그아웃할 이용자 조회
        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        String userId = jwtService.getIdFromAccess(accessToken);
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("user not found : " + userId));

        // 시큐리티 컨텍스트 초기화
        SecurityContextHolder.clearContext();

        // JWT 쿠키 삭제
        jwtService.logout(user, response);
        return ResponseEntity.ok("LOGOUT");
    }
}
