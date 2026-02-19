package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CupomRepositoryImpl implements CupomRepository {

    private final CupomJpaRepository cupomJpaRepository;

    public CupomRepositoryImpl(CupomJpaRepository cupomJpaRepository) {
        this.cupomJpaRepository = cupomJpaRepository;
    }


    @Override
    public Cupom save(Cupom cupom) {
        CupomEntity cupomEntity = this.toEntity(cupom);
        CupomEntity saved = this.cupomJpaRepository.save(cupomEntity);
        return this.toDomain(saved);
    }

    @Override
    public Optional<Cupom> findById(Long id) {
        return cupomJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cupom> findByCode(String code) {
        return cupomJpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return cupomJpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCodeWithLock(String code) {
        return cupomJpaRepository.existsByCodeWithLock(code);
    }

    private CupomEntity toEntity(Cupom cupom) {
        CupomEntity entity = new CupomEntity();
        entity.setId(cupom.getId());
        entity.setCode(cupom.getCode());
        entity.setDescription(cupom.getDescription());
        entity.setDiscountValue(cupom.getDiscountValue());
        entity.setExpirationDate(cupom.getExpirationDate());
        entity.setPublished(cupom.isPublished());
        entity.setDeletedAt(cupom.getDeletedAt());
        entity.setVersion(cupom.getVersion());
        return entity;
    }
    private Cupom toDomain(CupomEntity entity) {

        Cupom coupon = new Cupom(
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.isPublished(),
                entity.getDeletedAt(),
                entity.getVersion()
        );
        coupon.setId(entity.getId());
        coupon.setVersion(entity.getVersion());


        return coupon;
    }

}
