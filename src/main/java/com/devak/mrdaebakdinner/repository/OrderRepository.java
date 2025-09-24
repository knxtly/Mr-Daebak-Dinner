package com.devak.mrdaebakdinner.repository;

import com.devak.mrdaebakdinner.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByCustomerId(Long customerId);
    List<OrderEntity> findByStatusIn(List<String> statuses);
}
