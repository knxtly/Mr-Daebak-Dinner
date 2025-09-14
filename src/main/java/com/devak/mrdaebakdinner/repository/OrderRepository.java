package com.devak.mrdaebakdinner.repository;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository extends JpaRepository<OrderEntity, Long>{
}
