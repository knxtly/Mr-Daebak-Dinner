package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.CustomerDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.exception.CustomerNotFoundException;
import com.devak.mrdaebakdinner.exception.DatabaseException;
import com.devak.mrdaebakdinner.exception.DuplicateCustomerIdException;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import com.devak.mrdaebakdinner.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void signUp(CustomerDTO customerDTO) {
        // 이미 존재하는 ID인지 확인
        if (customerRepository.findByCustomerId(customerDTO.getCustomerId()).isPresent()) {
            throw new DuplicateCustomerIdException("이미 존재하는 사용자입니다.");
        }
        try {
            // 존재하지 않는 ID면 회원가입 시도
            CustomerEntity customerEntity = CustomerEntity.toCustomerEntity(customerDTO);
            customerRepository.save(customerEntity);
        } catch (RuntimeException e) {
            throw new DatabaseException("DB Error");
        }
    }

    // Controller로부터 온 login요청 처리
    public CustomerDTO login(CustomerDTO customerDTO) {
        Optional<CustomerEntity> result =
                customerRepository.findByCustomerId(customerDTO.getCustomerId());

        if (result.isPresent()) {
            // ID exists
            CustomerEntity customerEntity = result.get(); // .get(): Optional에서 꺼냄
            if (customerEntity.getPassword().equals(customerDTO.getPassword())) {
                // correct password
                return CustomerDTO.toCustomerDTO(customerEntity);
            } else {
                // incorrect password
                throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            // ID doesn't exist
            throw new CustomerNotFoundException("ID가 존재하지 않습니다.");
        }
    }


}
