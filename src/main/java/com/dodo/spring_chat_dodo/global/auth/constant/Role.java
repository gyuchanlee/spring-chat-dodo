package com.dodo.spring_chat_dodo.global.auth.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    NOT_REGISTERED("ROLE_NOT_REGISTERED", "회원가입 이전 사용자"),
    USER("ROLE_USER", "일반 회원"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
