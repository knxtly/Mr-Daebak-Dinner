package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.entity.InventoryEntity;
import com.devak.mrdaebakdinner.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public void incrementCount(Long itemId, int amount) {
        InventoryEntity inventoryEntity = inventoryRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 item입니다."));
        inventoryEntity.setStockQuantity(inventoryEntity.getStockQuantity() + amount);
        inventoryRepository.save(inventoryEntity);
    }

    public void decrementCount(Long itemId, int amount) {
        InventoryEntity inventoryEntity = inventoryRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 item입니다."));
        inventoryEntity.setStockQuantity(inventoryEntity.getStockQuantity() - amount);
        inventoryRepository.save(inventoryEntity);
    }
}
