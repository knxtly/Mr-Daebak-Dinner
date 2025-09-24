package com.devak.mrdaebakdinner.repository;

import com.devak.mrdaebakdinner.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    // JpaRepository<T, ID>
    // T: Entity 타입, ID: Entity의 PK타입
    /* JPA 기능들
    save(entity) => 저장/수정
    findById(id) => ID로 조회
    findAll() => 전체 조회
    delete(entity) => 삭제
    count() => 개수 조회
     */
    Optional<CustomerEntity> findByLoginId(String loginId);
}
