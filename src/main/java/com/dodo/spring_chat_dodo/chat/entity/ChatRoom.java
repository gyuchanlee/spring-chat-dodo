package com.dodo.spring_chat_dodo.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id", nullable = false, unique = true)
    private Long id;
    // 방 이름
    @Column(length = 100)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer participantsCount;

    // 비밀방 여부 -> 추후 기능 추가
    private boolean isPrivate = false;

}
