package com.dodo.spring_chat_dodo.chat.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomUpdateDto {

    @NotNull(message = "채팅방 아이디값은 필수")
    private Long id;
    
    @NotBlank(message = "채팅방 이름은 필수입니다")
    @Size(min = 2, max = 100, message = "채팅방 이름은 2자 이상 50자 이하로 입력해주세요")
    private String name;

    @Size(max = 255, message = "채팅방 설명은 255자 이하로 입력해주세요")
    private String description;

    @NotNull(message = "공개/비공개 여부는 필수입니다")
    private Boolean isPrivate;
}
