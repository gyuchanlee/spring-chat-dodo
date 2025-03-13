package com.dodo.spring_chat_dodo.chat.entity;

import com.dodo.spring_chat_dodo.global.entity.BaseCreatedTimeEntity;
import com.dodo.spring_chat_dodo.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomUser extends BaseCreatedTimeEntity {

    @EmbeddedId
    private ChatRoomUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    // 방 호스트인지 여부
    @Column(nullable = false)
    private boolean isHost = false;

}
