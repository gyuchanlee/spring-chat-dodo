package com.dodo.spring_chat_dodo.chat.entity;

import com.dodo.spring_chat_dodo.global.entity.BaseTimeEntity;
import com.dodo.spring_chat_dodo.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meesage_id", nullable = false, unique = true)
    private Long id;

    // 메시지 발신인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 메시지를 받는 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    private String message;

    private Integer unreadCount;
}
