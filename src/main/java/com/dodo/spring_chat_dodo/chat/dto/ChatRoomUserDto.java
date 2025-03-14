package com.dodo.spring_chat_dodo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 챗 메시지 반환 Dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUserDto {

    private Long userId;
    private Long chatRoomId;
    private boolean isHost;
}
