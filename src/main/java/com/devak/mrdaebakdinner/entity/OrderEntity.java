package com.devak.mrdaebakdinner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "orders") // order가 db에서 예약어라서 "orders" 쓰래
@Getter
@Setter
public class OrderEntity {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(name = "order_time", nullable = false, updatable = false)
    private OffsetDateTime orderTime;

    @ManyToOne(fetch = FetchType.LAZY) // n:1관계. Entity 타입이어야 함
    @JoinColumn(name = "customer_id") // <필드명>_<참조 PK 컬럼명>
    private CustomerEntity customer;

    @Column(name = "dinner_kind", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderDnrKind dinnerKind;

    @Column(name = "dinner_style", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderDnrStyle dinnerStyle;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_time")
    private OffsetDateTime deliveryTime;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @PrePersist
    public void prePersist() {
        if (totalPrice == null) {
            totalPrice = 0;
        }
        if (status == null) {
            status = OrderStatus.ORDERED;
        }
        if (orderTime == null) {
            orderTime = OffsetDateTime.now(ZoneOffset.ofHours(9));
        }
    }
}
