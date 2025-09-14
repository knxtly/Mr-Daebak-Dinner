package com.devak.mrdaebakdinner.dto;

import com.devak.mrdaebakdinner.entity.CustomerEntity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerDTO {
    private Long id;
    @NotBlank
    private String customerId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    private String contact;
    private int orderCount;
    private String membershipLevel;

    // CustomerEntity => CustomerDTO
    public static CustomerDTO toCustomerDTO(CustomerEntity customerEntity) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerEntity.getId());
        customerDTO.setCustomerId(customerEntity.getCustomerId());
        customerDTO.setName(customerEntity.getName());
        customerDTO.setAddress(customerEntity.getAddress());
        customerDTO.setContact(customerEntity.getContact());
        customerDTO.setPassword(customerEntity.getPassword());
        customerDTO.setOrderCount(customerEntity.getOrderCount());
        customerDTO.setMembershipLevel(customerEntity.getMembershipLevel());

        return customerDTO;
    }
}
