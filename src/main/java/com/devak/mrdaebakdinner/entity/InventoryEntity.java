package com.devak.mrdaebakdinner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class InventoryEntity {
    @Id
    @Column(name = "item_id")
    private Long itemId;

    @OneToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    private int stockQuantity; // count는 PostgreSQL에서 예약어라서 피하기
}
