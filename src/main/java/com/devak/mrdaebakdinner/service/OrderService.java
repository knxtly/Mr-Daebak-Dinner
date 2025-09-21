package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.dto.OrderHistoryDTO;
import com.devak.mrdaebakdinner.dto.OrderItemDTO;
import com.devak.mrdaebakdinner.entity.*;
import com.devak.mrdaebakdinner.mapper.OrderMapper;
import com.devak.mrdaebakdinner.repository.CustomerRepository;
import com.devak.mrdaebakdinner.repository.ItemRepository;
import com.devak.mrdaebakdinner.repository.OrderItemRepository;
import com.devak.mrdaebakdinner.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

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
    public void placeOrder(OrderDTO orderDTO,
                           OrderItemDTO orderItemDTO,
                           CustomerLoginDTO customerLoginDTO) {
        // TODO: 재고가 불충분 = 주문불가

        // 고객의 id찾고 orderCount 1증가. membership update
        CustomerEntity customerEntity = customerRepository.findByLoginId(customerLoginDTO.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다."));
        customerEntity.setOrderCount(customerEntity.getOrderCount() + 1);
        if (customerEntity.getOrderCount() >= 5)
            customerEntity.setMembershipLevel("VIP");

        // save Order
        OrderEntity savedOrder =
                orderRepository.save(OrderMapper.toOrderEntity(orderDTO, customerEntity));

        // save OrderItem
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : orderItemDTO.getOrderItems().entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();

            ItemEntity item = itemRepository.findByName(itemName)
                    .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다." + itemName));

            OrderItemEntity orderItemEntity = new OrderItemEntity();
            // OrderItemId를 직접 생성해서 넣어야 한다: JPA가 @MapsId 때문에 id를 Long으로 채우려고 하기 때문
            OrderItemId orderItemId = new OrderItemId(savedOrder.getId(), item.getId());
            orderItemEntity.setId(orderItemId);

            // 나머지는 그냥 넣어도 됨
            orderItemEntity.setOrder(savedOrder);
            orderItemEntity.setItem(item);
            orderItemEntity.setQuantity(quantity);

            orderItemEntityList.add(orderItemEntity);
        }
        orderItemRepository.saveAll(orderItemEntityList);

        // orderCount 1 증가 반영하기 (영속 상태이기 때문에 자동 반영되지만, 명시적 호출)
        customerRepository.save(customerEntity);
    }

    public OrderDTO buildOrderDTO(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));
        return OrderMapper.toOrderDTO(orderEntity);
    }

    public OrderItemDTO buildOrderItemDTO(Long orderId) {
        // order_item테이블에서 orderId가 일치하는 레코드 모두 가져옴
        List<OrderItemEntity> orderItemList = orderItemRepository.findAllByOrderId(orderId);

        Map<String, Integer> itemMap = orderItemList.stream()
                .collect(Collectors.toMap(
                        oi -> oi.getItem().getName(), // Key = Item Name
                        OrderItemEntity::getQuantity // Value = quantity
                ));

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItems(itemMap);
        return dto;
    }

}
