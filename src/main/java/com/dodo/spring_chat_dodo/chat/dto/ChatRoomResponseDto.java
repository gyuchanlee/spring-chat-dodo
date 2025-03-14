package com.dodo.spring_chat_dodo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;
    private String name;
    private String description;
    private Integer participantsCount;
    private Boolean isPrivate;
    // 위도
    private Double latitude;
    // 경도
    private Double longitude;
}
