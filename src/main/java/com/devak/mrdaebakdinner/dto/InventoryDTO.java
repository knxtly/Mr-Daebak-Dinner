package com.devak.mrdaebakdinner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class InventoryDTO {
    // Inventory 조회에 쓸 예정
    private Long itemId;
    private int string;
}
