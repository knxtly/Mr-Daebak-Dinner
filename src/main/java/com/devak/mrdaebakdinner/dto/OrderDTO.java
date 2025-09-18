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
    @NotNull
    private LocalDateTime orderTime;
    // 보통 DTO에서는 **id(Long)**만 들고, 서비스에서 Entity로 변환하는 것이 깔끔
    // 변환 시 서비스에서 customerId로 DB에서 CustomerEntity 조회 후 toOrderEntity에 전달
    @NotNull
    private Long customerId;
    @NotBlank
    private String dinnerKind;
    @NotBlank
    private String dinnerStyle;
    @NotBlank
    private String deliveryAddress;
    private LocalDateTime deliveryTime;
    @NotNull
    private Integer totalPrice;
    @NotBlank
    private String cardNumber;
    private String status;

    // OrderEntity => OrderDTO
    public static OrderDTO toOrderDTO(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setOrderTime(orderEntity.getOrderTime());
        orderDTO.setCustomerId(orderEntity.getCustomer().getId());
        orderDTO.setDinnerKind(orderEntity.getDinnerKind());
        orderDTO.setDinnerStyle(orderEntity.getDinnerStyle());
        orderDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderDTO.setDeliveryTime(orderEntity.getDeliveryTime());
        orderDTO.setTotalPrice(orderEntity.getTotalPrice());
        orderDTO.setCardNumber(orderEntity.getCardNumber());
        orderDTO.setStatus(orderEntity.getStatus());
        return orderDTO;
    }
}
