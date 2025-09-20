package com.devak.mrdaebakdinner.mapper;

import com.devak.mrdaebakdinner.dto.CustomerDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;

public class CustomerMapper {
    // CustomerEntity => CustomerDTO
    public static CustomerDTO toCustomerDTO(CustomerEntity customerEntity) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerEntity.getId());
        customerDTO.setLoginId(customerEntity.getLoginId());
        customerDTO.setPassword(customerEntity.getPassword());
        customerDTO.setName(customerEntity.getName());
        customerDTO.setAddress(customerEntity.getAddress());
        customerDTO.setContact(customerEntity.getContact());
        customerDTO.setOrderCount(customerEntity.getOrderCount());
        customerDTO.setMembershipLevel(customerEntity.getMembershipLevel());
        return customerDTO;
    }

    // CustomerDTO => CustomerEntity
    public static CustomerEntity toCustomerEntity(CustomerDTO customerDTO) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setLoginId(customerDTO.getLoginId());
        customerEntity.setPassword(customerDTO.getPassword());
        customerEntity.setName(customerDTO.getName());
        customerEntity.setAddress(customerDTO.getAddress());
        customerEntity.setContact(customerDTO.getContact());
        // id, orderCount, membershipLevel은 DB 기본값 사용
        return customerEntity;
    }
}
