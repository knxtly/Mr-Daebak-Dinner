package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.*;
import com.devak.mrdaebakdinner.entity.*;
import com.devak.mrdaebakdinner.exception.InsufficientInventoryException;
import com.devak.mrdaebakdinner.mapper.OrderMapper;
import com.devak.mrdaebakdinner.repository.*;
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
    private final InventoryRepository inventoryRepository;

    public List<OrderHistoryDTO> findOrderHistoryByLoginId(String loginId) {
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

    public OrderHistoryDTO findOrderHistoryByOrderId(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        return OrderMapper.toOrderHistoryDTO(order);
    }

    public OrderItemDTO findOrderItemByOrderId(Long orderId) {
        // orderId에 해당하는 item
        List<OrderItemEntity> orderItems = orderItemRepository.findAllByOrderId(orderId);

        // Map<String, Integer>로 변환 (key: item name, value: quantity)
        Map<String, Integer> itemMap = orderItems.stream()
                .collect(Collectors.toMap(
                        oi -> oi.getItem().getName(),
                        OrderItemEntity::getQuantity
                ));

        // DTO에 담아 반환
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItems(itemMap);
        return dto;
    }

    @Transactional
    public void placeOrder(OrderDTO orderDTO,
                           OrderItemDTO orderItemDTO,
                           CustomerSessionDTO customerSessionDTO) {
        // 고객의 id찾고 orderCount 1증가. membership update
        CustomerEntity customerEntity = customerRepository.findByLoginId(customerSessionDTO.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다."));
        customerEntity.setOrderCount(customerEntity.getOrderCount() + 1);
        if (customerEntity.getOrderCount() >= 5)
            customerEntity.setMembershipLevel("VIP");

        // save Order
        OrderEntity savedOrder =
                orderRepository.save(OrderMapper.toOrderEntity(orderDTO, customerEntity));

        // save OrderItem
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        // 부족한 재고의 이름을 모은 리스트
        List<String> insufficientItems = new ArrayList<>();

        // OrderItemDTO 내부 반복
        for (Map.Entry<String, Integer> entry : orderItemDTO.getOrderItems().entrySet()) {
            String orderItemName = entry.getKey();
            int quantity = entry.getValue();

            ItemEntity item = itemRepository.findByName(orderItemName)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "없는 item입니다." + orderItemName
                    ));

            InventoryEntity inventoryEntity = inventoryRepository.findByItemId(item.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "재고에 등록되지 않은 item입니다." + orderItemName
                    ));

            // 재고가 불충분하면 주문불가
            if (inventoryEntity.getStockQuantity() < quantity) {
                insufficientItems.add(orderItemName + " (요청: " + quantity +
                        ", 보유: " + inventoryEntity.getStockQuantity() + ")\n");
                continue;
            }

            // 재고가 충분하면 quantity만큼 decrease
            inventoryEntity.setStockQuantity(inventoryEntity.getStockQuantity() - quantity);

            // OrderItemEntity 구성 후 저장
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            OrderItemId orderItemId = new OrderItemId(savedOrder.getId(), item.getId());
            orderItemEntity.setId(orderItemId); // OrderItemId(PK)를 직접 생성해 넣음
            // -> JPA가 @MapsId 때문에 id를 Long으로 채우려고 하기 때문
            orderItemEntity.setOrder(savedOrder); // 나머지는 그냥 넣어도 됨
            orderItemEntity.setItem(item);
            orderItemEntity.setQuantity(quantity);

            orderItemEntityList.add(orderItemEntity);
        }

        // 재고가 부족한 게 있었다면 예외 호출
        if (!insufficientItems.isEmpty()) {
            throw new InsufficientInventoryException("재고가 부족합니다", insufficientItems);
        }

        // orderItemEntity 저장
        orderItemRepository.saveAll(orderItemEntityList);

        // 영속 상태이기 때문에 아래 변경사항은 자동 반영됨
        // customerEntity: orderCount 1
        // inventoryEntity: stockQuantity 주문량만큼 감소
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
