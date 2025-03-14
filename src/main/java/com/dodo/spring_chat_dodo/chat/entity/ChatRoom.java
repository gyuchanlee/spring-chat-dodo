package com.dodo.spring_chat_dodo.chat.entity;

import com.dodo.spring_chat_dodo.chat.dto.ChatRoomUpdateDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private Boolean isPrivate = false;
    // 위도
    private Double latitude;
    // 경도
    private Double longitude;

    // ChatRoomUser 엔티티와의 양방향 관계 설정 및 CASCADE 추가
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();


    // Message 엔티티와의 양방향 관계 설정 및 CASCADE 추가
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // 업데이트 메서드
    public void update(ChatRoomUpdateDto chatRoomUpdateDto) {
        this.name = chatRoomUpdateDto.getName();
        this.description = chatRoomUpdateDto.getDescription();
        this.isPrivate = chatRoomUpdateDto.getIsPrivate();
    }


    // 참여자 수 증가
    public void increaseParticipantsCount() {
        if (this.participantsCount <= 20) {
            this.participantsCount++;
        }
    }

    // 참여자 수 감소
    public void decreaseParticipantsCount() {
        if (this.participantsCount > 0) {
            this.participantsCount--;
        }
    }
}
