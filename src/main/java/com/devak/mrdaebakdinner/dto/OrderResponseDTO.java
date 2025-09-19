package com.devak.mrdaebakdinner.dto;

import com.devak.mrdaebakdinner.entity.OrderEntity;
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
public class OrderResponseDTO { // 주문조회 시 정보를 담아올 객체
    private Long id;
    @NotNull
    private LocalDateTime orderTime;
    @NotBlank
    private String customerLoginId;
    @NotBlank
    private String customerName;
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
    private String status;

    public static OrderResponseDTO toOrderResponseDTO(OrderEntity orderEntity) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(orderEntity.getId());
        dto.setOrderTime(orderEntity.getOrderTime());
        dto.setCustomerLoginId(orderEntity.getCustomer().getLoginId());
        dto.setCustomerName(orderEntity.getCustomer().getName());
        dto.setDinnerKind(orderEntity.getDinnerKind());
        dto.setDinnerStyle(orderEntity.getDinnerStyle());
        dto.setDeliveryAddress(orderEntity.getDeliveryAddress());
        dto.setDeliveryTime(orderEntity.getDeliveryTime());
        dto.setTotalPrice(orderEntity.getTotalPrice());
        dto.setCardNumber(orderEntity.getCardNumber());
        dto.setStatus(orderEntity.getStatus());
        return dto;
    }
}
