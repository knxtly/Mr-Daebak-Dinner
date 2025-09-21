package com.devak.mrdaebakdinner.repository;

import com.devak.mrdaebakdinner.entity.OrderItemEntity;
import com.devak.mrdaebakdinner.entity.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, OrderItemId> {
    // 특정 order의 모든 item 조회
    List<OrderItemEntity> findAllByOrderId(Long orderId);
    // 특정 item이 들어간 모든 order 조회
//    List<OrderItemEntity> findAllByItemId(Long itemId);
}
