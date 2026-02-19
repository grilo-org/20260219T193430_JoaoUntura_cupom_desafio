package com.example.demo.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CupomJpaRepository extends JpaRepository<CupomEntity, Long> {
    Optional<CupomEntity> findByCode(String code);
    boolean existsByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CupomEntity c WHERE c.code = :code")
    boolean existsByCodeWithLock(String code);
}
