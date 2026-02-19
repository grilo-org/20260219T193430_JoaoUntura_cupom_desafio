package com.example.demo.domain;



import com.example.demo.domain.exceptions.InvalidDiscountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Discount Tests")
class DiscountTest {

    @Test
    @DisplayName("Deve criar desconto com valor válido")
    void shouldCreateDiscountWithValidValue() {
        BigDecimal value = new BigDecimal("10.00");

        Discount discount = new Discount(value);

        assertEquals(value, discount.getValue());
    }

    @Test
    @DisplayName("Deve criar desconto com valor mínimo permitido")
    void shouldCreateDiscountWithMinimumValue() {
        BigDecimal minValue = new BigDecimal("0.5");

        Discount discount = new Discount(minValue);

        assertEquals(minValue, discount.getValue());
    }

    @Test
    @DisplayName("Deve criar desconto com valor grande")
    void shouldCreateDiscountWithLargeValue() {
        BigDecimal value = new BigDecimal("99.99");

        Discount discount = new Discount(value);

        assertEquals(value, discount.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor é nulo")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(
                InvalidDiscountException.class,
                () -> new Discount(null),
                "Valor do desconto não pode ser nulo"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor é menor que mínimo permitido")
    void shouldThrowExceptionWhenValueIsBelowMinimum() {
        BigDecimal belowMinValue = new BigDecimal("0.49");

        InvalidDiscountException exception = assertThrows(
                InvalidDiscountException.class,
                () -> new Discount(belowMinValue)
        );

        assertTrue(exception.getMessage().contains("0.5"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor é zero")
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(
                InvalidDiscountException.class,
                () -> new Discount(BigDecimal.ZERO)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor é negativo")
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(
                InvalidDiscountException.class,
                () -> new Discount(new BigDecimal("-10.00"))
        );
    }

    @Test
    @DisplayName("Deve retornar o valor do desconto")
    void shouldReturnDiscountValue() {
        BigDecimal expectedValue = new BigDecimal("5.50");
        Discount discount = new Discount(expectedValue);

        assertEquals(expectedValue, discount.getValue());
    }
}
