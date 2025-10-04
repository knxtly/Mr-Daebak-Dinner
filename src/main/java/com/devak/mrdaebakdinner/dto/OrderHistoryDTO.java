package com.devak.mrdaebakdinner.dto;

import com.devak.mrdaebakdinner.entity.OrderDnrKind;
import com.devak.mrdaebakdinner.entity.OrderDnrStyle;
import com.devak.mrdaebakdinner.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderHistoryDTO { // 고객이 이전주문조회 시 정보를 담아올 객체
    private Long id;
    private String customerLoginId;
    @NotNull
    private ZonedDateTime orderTime;
    @NotNull
    private OrderDnrKind dinnerKind;
    @NotNull
    private OrderDnrStyle dinnerStyle;
    @NotBlank
    private String deliveryAddress;
    private ZonedDateTime deliveryTime;
    private Integer totalPrice;
    @NotBlank
    private String cardNumber;
    private OrderStatus status;
}
