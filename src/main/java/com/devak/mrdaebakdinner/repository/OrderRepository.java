package com.devak.mrdaebakdinner.repository;

import com.devak.mrdaebakdinner.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByCustomerId(Long customerId);
}
