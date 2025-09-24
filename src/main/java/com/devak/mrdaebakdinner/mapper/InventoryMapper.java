package com.devak.mrdaebakdinner.mapper;

import com.devak.mrdaebakdinner.dto.InventoryDTO;
import com.devak.mrdaebakdinner.entity.InventoryEntity;

public class InventoryMapper {
    // InventoryEntity => InventoryDTO
    public static InventoryDTO toInventoryDTO(InventoryEntity inventoryEntity) {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setItemId(inventoryEntity.getItemId());
        inventoryDTO.setItemName(inventoryEntity.getItem().getName());
        inventoryDTO.setQuantity(inventoryEntity.getStockQuantity());
        return inventoryDTO;
    }
}
