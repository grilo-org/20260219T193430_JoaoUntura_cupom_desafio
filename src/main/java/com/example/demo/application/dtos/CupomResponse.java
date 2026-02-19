package com.example.demo.application.dtos;


import com.example.demo.domain.Cupom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CupomResponse(
        Long id,
        String code,
        String description,
        BigDecimal discountValue,
        LocalDateTime expirationDate,
        boolean published,
        LocalDateTime deletedAt
) {
    public static CupomResponse from(Cupom coupon) {
        return new CupomResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.isPublished(),
                coupon.getDeletedAt()
        );
    }
}
