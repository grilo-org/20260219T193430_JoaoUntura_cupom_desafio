package com.example.demo.domain;


import com.example.demo.domain.exceptions.CoupomAlreadyDeletedException;
import com.example.demo.domain.exceptions.InvalidCupomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cupom Tests")
class CupomTest {

    private String validCode;
    private String validDescription;
    private BigDecimal validDiscount;
    private LocalDateTime validExpirationDate;

    @BeforeEach
    void setUp() {
        validCode = "ABC123";
        validDescription = "Cupom de desconto válido";
        validDiscount = new BigDecimal("10.00");
        validExpirationDate = LocalDateTime.now().plusDays(7);
    }

    @Test
    @DisplayName("Deve criar cupom com valores válidos")
    void shouldCreateCupomWithValidValues() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);

        assertEquals("ABC123", cupom.getCode());
        assertEquals("Cupom de desconto válido", cupom.getDescription());
        assertEquals(validDiscount, cupom.getDiscountValue());
        assertEquals(validExpirationDate, cupom.getExpirationDate());
        assertTrue(cupom.isPublished());
        assertFalse(cupom.isDeleted());
        assertEquals(0L, cupom.getVersion());
    }

    @Test
    @DisplayName("Deve criar cupom não publicado")
    void shouldCreateUnpublishedCupom() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, false);

        assertFalse(cupom.isPublished());
    }

    @Test
    @DisplayName("Deve lançar exceção quando código é inválido")
    void shouldThrowExceptionWhenCodeIsInvalid() {
        assertThrows(
                Exception.class,
                () -> new Cupom("AB", validDescription, validDiscount, validExpirationDate, true)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição é nula")
    void shouldThrowExceptionWhenDescriptionIsNull() {
        InvalidCupomException exception = assertThrows(
                InvalidCupomException.class,
                () -> new Cupom(validCode, null, validDiscount, validExpirationDate, true)
        );

        assertTrue(exception.getMessage().contains("nula"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição está em branco")
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        InvalidCupomException exception = assertThrows(
                InvalidCupomException.class,
                () -> new Cupom(validCode, "   ", validDiscount, validExpirationDate, true)
        );

        assertTrue(exception.getMessage().contains("nula"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição é vazia")
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThrows(
                InvalidCupomException.class,
                () -> new Cupom(validCode, "", validDiscount, validExpirationDate, true)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando desconto é inválido")
    void shouldThrowExceptionWhenDiscountIsInvalid() {
        assertThrows(
                Exception.class,
                () -> new Cupom(validCode, validDescription, new BigDecimal("0.1"), validExpirationDate, true)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de expiração é no passado")
    void shouldThrowExceptionWhenExpirationDateIsInPast() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        InvalidCupomException exception = assertThrows(
                InvalidCupomException.class,
                () -> new Cupom(validCode, validDescription, validDiscount, pastDate, true)
        );

        assertTrue(exception.getMessage().contains("passado"));
    }

    @Test
    @DisplayName("Deve criar cupom com data de expiração hoje")
    void shouldCreateCupomWithExpirationDateToday() {
        LocalDateTime today = LocalDateTime.now().plusSeconds(1);

        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, today, true);

        assertNotNull(cupom.getExpirationDate());
    }

    @Test
    @DisplayName("Deve deletar cupom não deletado")
    void shouldDeleteCupomWhenNotDeleted() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);
//        assertFalse(cupom.isDeleted());

        cupom.delete();

        assertTrue(cupom.isDeleted());
        assertNotNull(cupom.getDeletedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cupom já deletado")
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCupom() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);
        cupom.delete();

        CoupomAlreadyDeletedException exception = assertThrows(
                CoupomAlreadyDeletedException.class,
                cupom::delete
        );

        assertTrue(exception.getMessage().contains("já foi deletado"));
    }

    @Test
    @DisplayName("Deve verificar se cupom está deletado")
    void shouldVerifyIfCupomIsDeleted() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);
        assertFalse(cupom.isDeleted());

        cupom.delete();
        assertTrue(cupom.isDeleted());
    }


    @Test
    @DisplayName("Deve permitir setar ID após criação (para reconstrução do banco)")
    void shouldAllowSettingIdAfterCreation() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);

        cupom.setId(1L);

        assertEquals(1L, cupom.getId());
    }

    @Test
    @DisplayName("Deve permitir setar versão após criação (para reconstrução do banco)")
    void shouldAllowSettingVersionAfterCreation() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);

        cupom.setVersion(5L);

        assertEquals(5L, cupom.getVersion());
    }

    @Test
    @DisplayName("Deve criar cupom com construtor completo")
    void shouldCreateCupomWithCompleteConstructor() {
        LocalDateTime deletedAt = LocalDateTime.now();

        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true, deletedAt, 3L);

        assertEquals("ABC123", cupom.getCode());
        assertEquals("Cupom de desconto válido", cupom.getDescription());
        assertEquals(validDiscount, cupom.getDiscountValue());
        assertEquals(validExpirationDate, cupom.getExpirationDate());
        assertTrue(cupom.isPublished());
        assertTrue(cupom.isDeleted());
        assertEquals(deletedAt, cupom.getDeletedAt());
        assertEquals(3L, cupom.getVersion());
    }

    @Test
    @DisplayName("Deve criar cupom com construtor completo sem deleção")
    void shouldCreateCupomWithCompleteConstructorWithoutDeletion() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true, null, 2L);

        assertFalse(cupom.isDeleted());
        assertNull(cupom.getDeletedAt());
    }


    @Test
    @DisplayName("Deve retornar todos os valores do cupom")
    void shouldReturnAllCupomValues() {
        Cupom cupom = new Cupom(validCode, validDescription, validDiscount, validExpirationDate, true);

        assertEquals("ABC123", cupom.getCode());
        assertEquals("Cupom de desconto válido", cupom.getDescription());
        assertEquals(new BigDecimal("10.00"), cupom.getDiscountValue());
        assertEquals(validExpirationDate, cupom.getExpirationDate());
        assertTrue(cupom.isPublished());
        assertEquals(0L, cupom.getVersion());
    }

}
