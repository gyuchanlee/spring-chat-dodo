package com.dodo.spring_chat_dodo.user.service;

import com.dodo.spring_chat_dodo.user.dto.UserJoinDto;
import com.dodo.spring_chat_dodo.user.dto.UserResponseDto;
import com.dodo.spring_chat_dodo.user.dto.UserUpdateDto;
import com.dodo.spring_chat_dodo.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // 회원 한건 조회 by id
    UserResponseDto getUserById(Long userId);
    // 회원 한건 조회 by email
    UserResponseDto getUserByEmail(String email);
    // 회원 리스트 조회
    List<UserResponseDto> getUserList();
    // 회원 등록
    void joinUser(UserJoinDto userJoinDto);
    // 회원 수정
    void updateUser(UserUpdateDto userUpdateDto);
    // 회원 탈퇴
    void deleteUser(Long userId);
}
