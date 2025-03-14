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
public class ChatRoomCreateDto {

    @NotBlank(message = "채팅방 이름은 필수입니다")
    @Size(min = 2, max = 100, message = "채팅방 이름은 2자 이상 50자 이하로 입력해주세요")
    private String name;

    @Size(max = 255, message = "채팅방 설명은 255자 이하로 입력해주세요")
    private String description;

    @NotNull(message = "참여자 수는 필수입니다")
    @Min(value = 0, message = "참여자 수는 최소 0명 이상이어야 합니다")
    @Max(value = 20, message = "참여자 수는 최대 20명까지 가능합니다")
    private Integer participantsCount = 0;

    @NotNull(message = "공개/비공개 여부는 필수입니다")
    private Boolean isPrivate;
    // 위도 (-90 ~ 90)
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90도 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90도 이하이어야 합니다")
    private Double latitude;

    // 경도 (-180 ~ 180)
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180도 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180도 이하이어야 합니다")
    private Double longitude;
}
