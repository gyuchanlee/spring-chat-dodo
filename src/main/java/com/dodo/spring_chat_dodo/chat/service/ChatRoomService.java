package com.dodo.spring_chat_dodo.chat.service;

import com.dodo.spring_chat_dodo.chat.dto.*;
import com.dodo.spring_chat_dodo.chat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {

    // 채팅방 개설
    void createChatRoom(ChatRoomCreateDto chatRoomCreateDto);
    // 채팅방 정보 수정
    void updateChatRoom(ChatRoomUpdateDto chatRoomUpdateDto);
    // 채팅방 삭제
    void deleteChatRoom(Long chatRoomId);
    // 채팅방 인원 입장
    Long  joinChatRoom(Long chatRoomId);
    // 채팅방 인원 퇴장
    void leaveChatRoom(Long chatRoomId);
    // 채팅방 리스트 조회 (채팅방 정보만)
    List<ChatRoomResponseDto> getChatRooms();
    // 채팅방 한건 조회 with Message (채팅방에 입장하면 나오는 채팅들)
    List<MessageDto> getMessages(Long chatRoomId);

    MessageDto createMessage(MessageCreateDto messageCreateDto);
}
