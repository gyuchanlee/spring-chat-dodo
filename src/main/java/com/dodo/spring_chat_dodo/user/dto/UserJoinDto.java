package com.dodo.spring_chat_dodo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinDto {

    @NotBlank(message = "이름은 필수 입력값입니다")
    @Length(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요")
    private String username;
    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
            message = "비밀번호는 8~16자리수여야 하며, 영문, 숫자, 특수문자를 1개 이상 포함해야 합니다")
    private String password;
    private String profile;
}
