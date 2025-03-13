package com.dodo.spring_chat_dodo.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 *  BaseTimeEntity  --> BaseCreatedTimeEntity
 *  상속 구조
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 최상위 클래스에만 적용
@Getter
public abstract class BaseCreatedTimeEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

/**
 * 추상 클래스로 구현하는 이유
 * 1. 인스턴스화 방지 -> 상속화를 통해서만 쓰여져야하는 것임, 인스턴스화 해서 좋을게 없다
 * 2. JPA 스펙 준수 -> 이 클래스는 근본적으로 매핑 정보 저장을 위한 것임
 * 3. 상속 구조 보호 -> 상속 계층 구조로 엔티티가 상속받아 쓰는것 만을 강조할 수 있고, 독립적으로 잘못 쓰이는 걸 방지 가능
 * 4. 명확한 설계 의도 표현 -> 누가 봐도 그냥 엔티티로 쓰는게 아니라 매핑 클래스로 쓴다는걸 알 수 있음
 */