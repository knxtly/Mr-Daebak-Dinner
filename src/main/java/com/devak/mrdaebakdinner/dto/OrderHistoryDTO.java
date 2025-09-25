package com.devak.mrdaebakdinner.dto;

import com.devak.mrdaebakdinner.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderHistoryDTO { // 고객이 이전주문조회 시 정보를 담아올 객체
    private Long id;
    private String customerLoginId;
    @NotNull
    private LocalDateTime orderTime;
    @NotBlank
    private String dinnerKind;
    @NotBlank
    private String dinnerStyle;
    @NotBlank
    private String deliveryAddress;
    private LocalDateTime deliveryTime;
    private Integer totalPrice;
    @NotBlank
    private String cardNumber;
    private OrderStatus status;
}
