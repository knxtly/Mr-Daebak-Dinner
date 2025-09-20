package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
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

    public List<OrderHistoryDTO> findAllByLoginId(String loginId) {
        // loginId로 id 찾아서 반환
        CustomerEntity customerEntity = customerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다."));

        // id로 해당 고객의 주문만을 담아 return
        List<OrderEntity> orderEntityList =
                orderRepository.findAllByCustomerId(customerEntity.getId());
        return orderEntityList.stream()
                .map(OrderMapper::toOrderHistoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void placeOrder(OrderDTO orderDTO, CustomerLoginDTO customerLoginDTO) {
        // TODO: 재고가 불충분 = 주문불가

        CustomerEntity customerEntity = customerRepository.findByLoginId(customerLoginDTO.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다."));

        customerEntity.setOrderCount(customerEntity.getOrderCount() + 1);
        orderRepository.save(OrderMapper.toOrderEntity(orderDTO, customerEntity));

        // orderCount 1 증가 반영하기 (@Transactional 안의 영속 상태이기 때문에 자동 반영되지만, 명시적 호출)
        customerRepository.save(customerEntity);
    }

    public OrderDTO buildReorderDTO(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));
        return OrderMapper.toOrderDTO(orderEntity);
    }
}
