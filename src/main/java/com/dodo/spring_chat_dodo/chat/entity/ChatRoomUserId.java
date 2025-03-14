package com.dodo.spring_chat_dodo.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 중간 테이블 chat_room_users를 위한 복합 키 클래스
 */

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomUserId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "chat_room_id")
    private Long chatRoomId;
}
