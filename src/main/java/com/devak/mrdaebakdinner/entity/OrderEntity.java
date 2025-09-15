package com.devak.mrdaebakdinner.entity;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // order가 예약어라서 "orders" 쓰래
@Getter
@Setter
public class OrderEntity { // TODO: 각 특성 기본값, 외래키설정, 아키텍쳐설계부터
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId; // PK

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

    // Customer FK (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @Column(name = "delivery_menu", nullable = false)
    private String deliveryMenu; // FK => 일단 문자열로 저장

    @Column(name = "delivery_style", nullable = false)
    private String deliveryStyle;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "total_price")
    private Integer totalPrice;

    // TODO: toOrderEntity() 추가
    public OrderEntity toOrderEntity(OrderDTO orderDTO,
                                     HttpSession session) {
        CustomerEntity customerEntity =
                (CustomerEntity) session.getAttribute("loggedInCustomer");
        if (customerEntity == null) {
            throw new IllegalStateException("로그인 세션이 존재하지 않습니다.");
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderDTO.getOrderId());
        orderEntity.setOrderTime(orderDTO.getOrderTime());
        orderEntity.setCustomerEntity(customerEntity); // session으로부터 주문자 결정
        orderEntity.setDeliveryMenu(orderDTO.getDeliveryMenu());
        orderEntity.setDeliveryStyle(orderDTO.getDeliveryStyle());
        orderEntity.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderEntity.setCardNumber(orderDTO.getCardNumber());
//        orderEntity.setDeliveryTime(orderDTO.getDeliveryTime());
        orderEntity.setTotalPrice(orderDTO.getTotalPrice());

        return orderEntity;
    }
}
