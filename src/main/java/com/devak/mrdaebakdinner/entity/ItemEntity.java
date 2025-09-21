package com.devak.mrdaebakdinner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item")
@Getter
@Setter
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // item ID
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, name = "unit_price")
    private Double unitPrice; // 단가
}
