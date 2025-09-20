package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import com.devak.mrdaebakdinner.dto.CustomerSignUpDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.exception.CustomerNotFoundException;
import com.devak.mrdaebakdinner.exception.DatabaseException;
import com.devak.mrdaebakdinner.exception.DuplicateLoginIdException;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import com.devak.mrdaebakdinner.mapper.CustomerMapper;
import com.devak.mrdaebakdinner.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Controller로부터 온 login요청 처리
    public CustomerLoginDTO login(CustomerLoginDTO customerDTO) {
        Optional<CustomerEntity> byLoginId =
                customerRepository.findByLoginId(customerDTO.getLoginId());

        if (byLoginId.isPresent()) { // ID exists
            CustomerEntity customerEntity = byLoginId.get(); // .get(): Optional에서 꺼냄
            if (customerEntity.getPassword().equals(customerDTO.getPassword())) {
                // correct password
                return CustomerMapper.toCustomerLoginDTO(customerEntity);
            } else { // incorrect password
                throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
            }
        } else { // ID doesn't exist
            throw new CustomerNotFoundException("ID가 존재하지 않습니다.");
        }
    }

    public void signUp(CustomerSignUpDTO customerSignUpDTO) {
        // 이미 존재하는 ID인지 확인
        if (customerRepository.findByLoginId(customerSignUpDTO.getLoginId()).isPresent()) {
            throw new DuplicateLoginIdException("이미 존재하는 사용자입니다.");
        }
        try { // 존재하지 않는 ID면 회원가입 시도
            customerRepository.save(CustomerMapper.toCustomerEntity(customerSignUpDTO));
        } catch (DataAccessException e) {
            throw new DatabaseException("DB 처리 중 오류", e);
        }
    }

}
