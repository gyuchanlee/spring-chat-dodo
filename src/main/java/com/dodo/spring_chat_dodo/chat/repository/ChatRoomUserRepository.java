package com.dodo.spring_chat_dodo.chat.repository;

import com.dodo.spring_chat_dodo.chat.entity.ChatRoomUser;
import com.dodo.spring_chat_dodo.chat.entity.ChatRoomUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserId> {

    // 특정 채팅방의 특정 사용자를 제외한 모든 참여자 조회
    List<ChatRoomUser> findByChatRoom_IdAndUser_IdNot(Long chatRoomId, Long userId);

    List<ChatRoomUser> findByChatRoom_Id(Long chatRoomId);

    // 특정 사용자가 참여한 모든 채팅방 관계 조회
    List<ChatRoomUser> findByUser_Id(Long userId);
}
