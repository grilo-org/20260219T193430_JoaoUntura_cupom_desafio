package com.example.demo.domain;

import java.util.Optional;

public interface CupomRepository {
    Cupom save(Cupom cupom);
    Optional<Cupom> findById(Long id);
    Optional<Cupom> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeWithLock(String code);

}
