package com.dodo.spring_chat_dodo.chat.repository;

import com.dodo.spring_chat_dodo.chat.entity.ChatRoom;
import com.dodo.spring_chat_dodo.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatRoom(ChatRoom chatRoom);
}
