package com.devak.mrdaebakdinner.entity;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // order가 db에서 예약어라서 "orders" 쓰래
@Getter
@Setter
public class OrderEntity {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY) // n:1관계. Entity 타입이어야 함
    @JoinColumn(name = "customer_id") // <필드명>_<참조 PK 컬럼명>
    private CustomerEntity customer;

    @Column(name = "dinner_kind", nullable = false)
    private String dinnerKind;

    @Column(name = "dinner_style", nullable = false)
    private String dinnerStyle;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "total_price") // TODO: nullable 추가할 것. 계산 후 값 결정
    private Integer totalPrice;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "status", nullable = false)
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "주문완료";
        }
    }

    // OrderDTO => OrderEntity
    public static OrderEntity toOrderEntity(OrderDTO orderDTO, CustomerEntity customerEntity) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomer(customerEntity);
        orderEntity.setDinnerKind(orderDTO.getDinnerKind());
        orderEntity.setDinnerStyle(orderDTO.getDinnerStyle());
        orderEntity.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderEntity.setDeliveryTime(orderDTO.getDeliveryTime());
        orderEntity.setTotalPrice(orderDTO.getTotalPrice());
        orderEntity.setCardNumber(orderDTO.getCardNumber());
        // id, orderTime, status은 기본값 사용
        return orderEntity;
    }
}
