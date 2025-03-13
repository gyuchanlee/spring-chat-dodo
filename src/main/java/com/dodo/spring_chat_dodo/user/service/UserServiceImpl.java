package com.dodo.spring_chat_dodo.user.service;

import com.dodo.spring_chat_dodo.global.auth.constant.Role;
import com.dodo.spring_chat_dodo.user.dto.UserJoinDto;
import com.dodo.spring_chat_dodo.user.dto.UserResponseDto;
import com.dodo.spring_chat_dodo.user.dto.UserUpdateDto;
import com.dodo.spring_chat_dodo.user.entity.User;
import com.dodo.spring_chat_dodo.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 한건 조회 by id
    @Override
    public UserResponseDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertUserToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found")); // 에러 던지기
    }
    // 회원 한건 조회 by email
    @Override
    public UserResponseDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertUserToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found")); // 에러 던지기
    }
    // 회원 리스트 조회
    @Override
    public List<UserResponseDto> getUserList() {
        return userRepository.findAll()
                .stream().map(this::convertUserToResponseDto)
                .collect(Collectors.toList());
    }
    // 회원 등록
    @Transactional
    @Override
    public void joinUser(UserJoinDto userJoinDto) {

        User user = User.builder()
                .email(userJoinDto.getEmail())
                .username(userJoinDto.getUsername())
                .password(passwordEncoder.encode(userJoinDto.getPassword()))
                .profile(userJoinDto.getProfile())
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }
    // 회원 수정
    @Transactional
    @Override
    public void updateUser(UserUpdateDto userUpdateDto) {

        // 사용자 조회
        User user = userRepository.findByEmail(userUpdateDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 바꿀 비밀번호가 존재 시, dto에 비밀번호 인코딩
        if (userUpdateDto.getPassword() != null) {
            userUpdateDto.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        // dirty checking 활용
        user.userUpdate(userUpdateDto);
    }
    // 회원 탈퇴
    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // User -> UserResponseDto 변환 메서드
    private UserResponseDto convertUserToResponseDto(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(user.getProfile())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
