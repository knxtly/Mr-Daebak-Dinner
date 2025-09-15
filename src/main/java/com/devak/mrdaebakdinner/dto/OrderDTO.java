package com.devak.mrdaebakdinner.dto;

import com.devak.mrdaebakdinner.entity.OrderEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO { //TODO: 데이터 아키텍쳐 수정 필요
    private Long orderId;           // PK (auto_increment)
    @NotBlank
    private LocalDateTime orderTime;    // 주문 시각 (NN)
    @NotBlank
    private Long customerId;        // 고객 ID (FK)
    @NotBlank
    private String deliveryMenu;    // 메뉴명 or 메뉴ID (FK, NN)
    @NotBlank
    private String deliveryStyle;   // 배달 방식 (예: 포장, 배달) (NN)
    @NotBlank
    private String deliveryAddress; // 배달 주소 (NN)
    @NotBlank
    private String cardNumber;      // 결제 카드 번호 (NN)
    private LocalDateTime deliveryTime; // 배달 완료 시간
    private Integer totalPrice;     // 총 가격

    // TODO: toOrderDTO() 추가
    public static OrderDTO toOrderDTO(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderEntity.getOrderId());
        orderDTO.setOrderTime(orderEntity.getOrderTime());
        orderDTO.setCustomerId(orderEntity.getOrderId());
        orderDTO.setDeliveryMenu(orderEntity.getDeliveryMenu());
        orderDTO.setDeliveryStyle(orderEntity.getDeliveryStyle());
        orderDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderDTO.setCardNumber(orderEntity.getCardNumber());
        orderDTO.setDeliveryTime(orderEntity.getDeliveryTime());
        orderDTO.setTotalPrice(orderEntity.getTotalPrice());
        return orderDTO;
    }
}
