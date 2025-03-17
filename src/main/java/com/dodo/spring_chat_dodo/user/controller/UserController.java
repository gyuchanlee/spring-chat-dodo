package com.dodo.spring_chat_dodo.user.controller;

import com.dodo.spring_chat_dodo.user.dto.UserJoinDto;
import com.dodo.spring_chat_dodo.user.dto.UserResponseDto;
import com.dodo.spring_chat_dodo.user.dto.UserUpdateDto;
import com.dodo.spring_chat_dodo.user.entity.User;
import com.dodo.spring_chat_dodo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
@Slf4j
public class UserController {

    private final UserService userService;

    // 특정 회원 정보 조회 -> id
    @GetMapping("{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // 특정 회원 정보 조회 -> email
    @GetMapping("{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok().body(user);
    }
    
    // 전체 회원 리스트 가져오기 (관리자용)
    @GetMapping("")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> userList = userService.getUserList();
        return ResponseEntity.ok().body(userList);
    }

    // 회원 등록
    @PostMapping("")
    public ResponseEntity<Void> joinUser(@RequestBody @Validated UserJoinDto userJoinDto) {
        userService.joinUser(userJoinDto);
        return ResponseEntity.ok().build();
    }

    // 회원 수정
    @PutMapping("{email}")
    public ResponseEntity<UserResponseDto> joinUser(@RequestBody @Validated UserUpdateDto userUpdateDto) {
        UserResponseDto updatedUser = userService.updateUser(userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }
    // 회원 탈퇴
    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
