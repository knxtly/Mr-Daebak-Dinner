package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.entity.OrderEntity;
import com.devak.mrdaebakdinner.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderDTO> findAllByCustomerId(CustomerEntity loggedInCustomer) {
        // loggedInCustomer의 주문만을 담아 return
        List<OrderEntity> orderEntityList =
                orderRepository.findAllByCustomerId(loggedInCustomer.getId());
        return orderEntityList.stream()
                .map(OrderDTO::toOrderDTO)
                .collect(Collectors.toList());
    }
}
