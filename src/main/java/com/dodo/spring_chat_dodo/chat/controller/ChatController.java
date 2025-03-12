package com.dodo.spring_chat_dodo.chat.controller;

import com.dodo.spring_chat_dodo.chat.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations template;

    // 채팅 리스트 반환
    @GetMapping("/chat/{id}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long id) {
        // 더미 데이터 todo DB연결, 추후 roomId에 따라서 DB에서 맞는 데이터 쿼리가 필요함
        log.info("======== {}", id);
        MessageDto testDto1 = new MessageDto(1L, "test1", "test1123");
        return ResponseEntity.ok().body(List.of(testDto1));
    }

    // 메시지 송신 및 수신 -> /pub 생략, 클라이언트에선 /pub/message 로 요청하기
    @MessageMapping("/message")
    public void receiveMessage(MessageDto messageDto) {
//        log.info("메시지 수신: {}", messageDto.getContent());
        // 메시지를 해당 채팅방 구독자들에게 전송
        template.convertAndSend("/sub/chatroom/1", messageDto);
        // void나 메시지 자체를 반환하기
        log.info("메시지 전송 완료: {}", messageDto.getContent());
    }
}
