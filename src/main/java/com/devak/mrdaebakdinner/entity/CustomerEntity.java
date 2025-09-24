package com.devak.mrdaebakdinner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer") // database에서 "customer" table
@Getter
@Setter
public class CustomerEntity {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id; // 내부 관리용 ID

    @Column(name = "login_id", unique = true, nullable = false, length = 30)
    private String loginId; // 외부 비즈니스용 ID

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "contact")
    private String contact;

    @Column(name = "order_count")
    private int orderCount = 0;

    @Column(name = "membership_level")
    private String membershipLevel;

    @PrePersist
    public void prePersist() {
        if (membershipLevel == null) {
            membershipLevel = "Family";
        }
    }
}
