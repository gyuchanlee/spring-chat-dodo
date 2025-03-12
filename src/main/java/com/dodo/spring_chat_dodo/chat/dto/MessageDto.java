package com.dodo.spring_chat_dodo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 챗 메시지 반환 Dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long messageId;
    private String username;
    private String content;
}
