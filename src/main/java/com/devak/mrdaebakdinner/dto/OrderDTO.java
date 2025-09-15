package com.devak.mrdaebakdinner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {
    private Long orderId;           // PK (auto_increment)
    private LocalDateTime orderTime;    // 주문 시각 (NN)
    private Long customerId;        // 고객 ID (FK)
    private String deliveryMenu;    // 메뉴명 or 메뉴ID (FK, NN)
    private String deliveryStyle;   // 배달 방식 (예: 포장, 배달) (NN)
    private String deliveryAddress; // 배달 주소 (NN)
    private String cardNumber;      // 결제 카드 번호 (NN)
    private LocalDateTime deliveryTime; // 배달 완료 시간
    private Integer totalPrice;     // 총 가격

    // TODO: toOrderDTO() 추가
}
