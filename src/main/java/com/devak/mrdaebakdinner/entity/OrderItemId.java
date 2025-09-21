package com.devak.mrdaebakdinner.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
/* @Embeddable 클래스에는 기본 Constructor, equals(), hashCode()가 반드시 필요 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long itemId;
}
