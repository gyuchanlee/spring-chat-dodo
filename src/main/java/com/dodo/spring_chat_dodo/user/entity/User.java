package com.dodo.spring_chat_dodo.user.entity;

import com.dodo.spring_chat_dodo.chat.entity.ChatRoomUser;
import com.dodo.spring_chat_dodo.global.auth.constant.Role;
import com.dodo.spring_chat_dodo.global.entity.BaseTimeEntity;
import com.dodo.spring_chat_dodo.user.dto.UserUpdateDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // 설정안했을때는 그냥 디폴트 이미지 쓰도록 저장
    private String profile = "default";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ChatRoomUser 엔티티와의 양방향 관계 설정 및 CASCADE 추가
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }

    public void userUpdate(UserUpdateDto updateDto) {
        // null이 아닌 값만 업데이트
        if (updateDto.getUsername() != null) {
            this.username = updateDto.getUsername();
        }

        if (updateDto.getEmail() != null) {
            this.email = updateDto.getEmail();
        }

        if (updateDto.getPassword() != null) {
            this.password = updateDto.getPassword();
        }

        if (updateDto.getProfile() != null) {
            this.profile = updateDto.getProfile();
        }
    }
}
