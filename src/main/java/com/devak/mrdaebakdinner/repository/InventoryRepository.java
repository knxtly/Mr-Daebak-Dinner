package com.devak.mrdaebakdinner.repository;

import com.devak.mrdaebakdinner.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    // itemId를 통해 (item_id, count) 조회
    Optional<InventoryEntity> findByItemId(Long itemId);
}
