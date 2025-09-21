package com.devak.mrdaebakdinner.mapper;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.dto.OrderHistoryDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.entity.OrderEntity;

public class OrderMapper {
    // OrderEntity => OrderDTO
    public static OrderDTO toOrderDTO(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setDinnerKind(orderEntity.getDinnerKind());
        orderDTO.setDinnerStyle(orderEntity.getDinnerStyle());
        orderDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderDTO.setCardNumber(orderEntity.getCardNumber());
        return orderDTO;
    }

    // OrderEntity => OrderHistoryDTO
    public static OrderHistoryDTO toOrderHistoryDTO(OrderEntity orderEntity) {
        OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
        orderHistoryDTO.setId(orderEntity.getId());
        orderHistoryDTO.setOrderTime(orderEntity.getOrderTime());
        orderHistoryDTO.setDinnerKind(orderEntity.getDinnerKind());
        orderHistoryDTO.setDinnerStyle(orderEntity.getDinnerStyle());
        orderHistoryDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderHistoryDTO.setDeliveryTime(orderEntity.getDeliveryTime());
        orderHistoryDTO.setTotalPrice(orderEntity.getTotalPrice());
        orderHistoryDTO.setCardNumber(orderEntity.getCardNumber());
        orderHistoryDTO.setStatus(orderEntity.getStatus());
        return orderHistoryDTO;
    }

    // OrderDTO => OrderEntity
    public static OrderEntity toOrderEntity(OrderDTO orderDTO, CustomerEntity customerEntity) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomer(customerEntity);
        orderEntity.setDinnerKind(orderDTO.getDinnerKind());
        orderEntity.setDinnerStyle(orderDTO.getDinnerStyle());
        orderEntity.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderEntity.setCardNumber(orderDTO.getCardNumber());
        // id, orderTime, status은 기본값 사용
        return orderEntity;
    }
}
