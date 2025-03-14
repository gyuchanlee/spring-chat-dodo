package com.dodo.spring_chat_dodo.chat.repository;

import com.dodo.spring_chat_dodo.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
