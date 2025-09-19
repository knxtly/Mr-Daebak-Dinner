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
public class OrderDTO {
    private Long id;
    private LocalDateTime orderTime;
    // 보통 DTO에서는 **id(Long)**만 들고, 서비스에서 Entity로 변환하는 것이 깔끔
    // 변환 시 Service에서 customerId로 DB에서 CustomerEntity 조회 후 toOrderEntity에 전달
    private Long customerId;
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

    // OrderEntity => OrderDTO
    public static OrderDTO toOrderDTO(OrderEntity orderEntity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(orderEntity.getId());
        dto.setOrderTime(orderEntity.getOrderTime());
        dto.setCustomerId(orderEntity.getCustomer().getId());
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
