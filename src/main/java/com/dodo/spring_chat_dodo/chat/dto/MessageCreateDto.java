package com.dodo.spring_chat_dodo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 챗 메시지 반환 Dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCreateDto {

    private String email;
    private Long chatRoomId;
    private String content;
}
