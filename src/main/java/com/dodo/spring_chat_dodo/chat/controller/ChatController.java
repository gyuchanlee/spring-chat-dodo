package com.dodo.spring_chat_dodo.chat.controller;

import com.dodo.spring_chat_dodo.chat.dto.*;
import com.dodo.spring_chat_dodo.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRoomService chatRoomService;

    // 한 채팅방의 채팅 리스트 반환
    @GetMapping("/{id}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long id) {
        List<MessageDto> messages = chatRoomService.getMessages(id);
        return ResponseEntity.ok(messages);
    }

    // 채팅방 개설
    @PostMapping("")
    public ResponseEntity<Void> createChatRoom(@RequestBody @Validated ChatRoomCreateDto chatRoomCreateDto) {
        chatRoomService.createChatRoom(chatRoomCreateDto);
        return ResponseEntity.ok().build();
    }

    // 채팅방 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateChatRoom(@RequestBody @Validated ChatRoomUpdateDto chatRoomUpdateDto) {
        chatRoomService.updateChatRoom(chatRoomUpdateDto);
        return ResponseEntity.ok().build();
    }

    // 채팅방 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long id) {
        chatRoomService.deleteChatRoom(id);
        return ResponseEntity.ok().build();
    }

    // 채팅방 인원 입장
    @PostMapping("/{chatRoomId}/joinUser")
    public ResponseEntity<Long> joinChatRoom(@PathVariable Long chatRoomId) {
        Long joinedChatRoom = chatRoomService.joinChatRoom(chatRoomId);
        return ResponseEntity.ok(joinedChatRoom);
    }

    // 채팅방 인원 퇴장 todo
    @DeleteMapping("/{chatRoomId}/leaveUser")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.leaveChatRoom(chatRoomId);
        return ResponseEntity.ok().build();
    }

    // 채팅방 리스트 조회 (채팅방 정보만) todo
    @GetMapping("")
    public ResponseEntity<List<ChatRoomResponseDto>> chatRooms() {
        return ResponseEntity.ok().body(chatRoomService.getChatRooms());
    }

    // 메시지 송신 및 수신 -> /pub 생략, 클라이언트에선 /pub/message 로 요청하기
    @MessageMapping("/message")
    public void receiveMessage(MessageCreateDto messageCreateDto) {

        MessageDto message = chatRoomService.createMessage(messageCreateDto);

        // 메시지를 해당 채팅방 구독자들에게 전송
        template.convertAndSend("/sub/chatroom/"+messageCreateDto.getChatRoomId(), message);
        // void 나 메시지 자체를 반환하기
        log.info("메시지 전송 완료: {}", message.getContent());
    }
}
