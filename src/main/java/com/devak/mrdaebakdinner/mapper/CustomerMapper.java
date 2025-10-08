package com.devak.mrdaebakdinner.mapper;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import com.devak.mrdaebakdinner.dto.CustomerSessionDTO;
import com.devak.mrdaebakdinner.dto.CustomerSignUpDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;

public class CustomerMapper {
    // CustomerEntity => CustomerLoginDTO
    public static CustomerLoginDTO toCustomerLoginDTO(CustomerEntity customerEntity) {
        CustomerLoginDTO customerDTO = new CustomerLoginDTO();
        customerDTO.setLoginId(customerEntity.getLoginId());
        customerDTO.setPassword(customerEntity.getPassword());
        return customerDTO;
    }

    // CustomerSignUpDTO => CustomerEntity
    public static CustomerEntity toCustomerEntity(CustomerSignUpDTO customerSignUpDTO) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setLoginId(customerSignUpDTO.getLoginId());
        customerEntity.setPassword(customerSignUpDTO.getPassword());
        customerEntity.setName(customerSignUpDTO.getName());
        customerEntity.setAddress(customerSignUpDTO.getAddress());
        customerEntity.setContact(customerSignUpDTO.getContact());
        // id, orderCount, membershipLevel은 DB 기본값 사용
        return customerEntity;
    }

    // CustomerEntity => CustomerSessionDTO
    public static CustomerSessionDTO toCustomerSessionDTO(CustomerEntity customerEntity) {
        CustomerSessionDTO customerSessionDTO = new CustomerSessionDTO();
        customerSessionDTO.setLoginId(customerEntity.getLoginId());
        customerSessionDTO.setName(customerEntity.getName());
        customerSessionDTO.setMembership(customerEntity.getMembershipLevel());
        return customerSessionDTO;
    }
}
