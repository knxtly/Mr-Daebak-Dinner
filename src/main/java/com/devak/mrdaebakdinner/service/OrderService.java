package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.CustomerDTO;
import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.dto.OrderHistoryDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.entity.OrderEntity;
import com.devak.mrdaebakdinner.mapper.OrderMapper;
import com.devak.mrdaebakdinner.repository.CustomerRepository;
import com.devak.mrdaebakdinner.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public List<OrderHistoryDTO> findAllByCustomerId(Long customerId) {
        // loggedInCustomer의 주문만을 담아 return
        List<OrderEntity> orderEntityList =
                orderRepository.findAllByCustomerId(customerId);
        return orderEntityList.stream()
                .map(OrderMapper::toOrderHistoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void placeOrder(@Valid OrderDTO orderDTO, CustomerDTO customerDTO) {
        // TODO: 재고가 불충분 = 주문불가
        CustomerEntity customer = customerRepository.findById(customerDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다."));

        customer.setOrderCount(customer.getOrderCount() + 1);

        orderRepository.save(
                OrderMapper.toOrderEntity(orderDTO, customer)
        );

        customerRepository.save(customer);
    }

    public @Valid OrderDTO buildReorderDTO(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다."));
        return OrderMapper.toOrderDTO(orderEntity);
    }
}
