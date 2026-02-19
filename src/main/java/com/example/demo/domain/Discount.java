package com.example.demo.domain;

import com.example.demo.domain.exceptions.InvalidDiscountException;

import java.math.BigDecimal;

public class Discount {

    private static final BigDecimal MIN_VALUE = new BigDecimal("0.5");
    private final BigDecimal value;


    public Discount(BigDecimal value) {
        this.validateValue(value);
        this.value = value;
    }

    private void validateValue(BigDecimal value){
        if(value == null){
            throw new InvalidDiscountException("Valor do desconto não pode ser nulo");
        }

        if(value.compareTo(MIN_VALUE)  < 0 ){
            throw new InvalidDiscountException("Valor do desconto deve ser no mínimio " + MIN_VALUE );
        }
    }

    public BigDecimal getValue() {
        return value;
    }
}
