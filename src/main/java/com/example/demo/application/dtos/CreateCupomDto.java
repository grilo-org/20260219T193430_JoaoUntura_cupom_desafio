package com.example.demo.application.dtos;

import jakarta.validation.constraints.NotBlank;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCupomDto (
        @NotBlank
        String code,
        @NotBlank String description,
        @NotNull
        BigDecimal discountValue,
        @NotNull LocalDateTime expirationDate,
        Boolean published
) { }
