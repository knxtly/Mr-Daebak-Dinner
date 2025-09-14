package com.devak.mrdaebakdinner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // order가 예약어라서 "orders" 쓰래
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId; // PK

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_menu", nullable = false)
    private String deliveryMenu; // FK → 일단 문자열로 저장

    @Column(name = "delivery_style", nullable = false)
    private String deliveryStyle;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    // Customer FK (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "total_price")
    private Integer totalPrice;

    // TODO: toOrderEntity() 추가
}
