package com.devak.mrdaebakdinner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class InventoryDTO { // inventory 조회에 사용
    private Long itemId;
    private String itemName;
    private int quantity;
}
