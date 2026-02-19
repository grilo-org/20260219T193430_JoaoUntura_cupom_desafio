package com.example.demo.domain;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CupomRepository Tests")
@ExtendWith(MockitoExtension.class)
class CupomRepositoryTest {

    @Mock
    private CupomRepository cupomRepository;

    private Cupom cupom;

    @BeforeEach
    void setUp() {
        cupom = new Cupom(
                "ABC123",
                "Desconto válido",
                new BigDecimal("10.00"),
                LocalDateTime.now().plusDays(7),
                true
        );
        cupom.setId(1L);
    }

    @Test
    @DisplayName("Deve salvar cupom")
    void shouldSaveCupom() {
        when(cupomRepository.save(cupom)).thenReturn(cupom);

        Cupom saved = cupomRepository.save(cupom);

        assertNotNull(saved);
        assertEquals(cupom.getCode(), saved.getCode());
        verify(cupomRepository, times(1)).save(cupom);
    }

    @Test
    @DisplayName("Deve encontrar cupom por ID")
    void shouldFindCupomById() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));

        Optional<Cupom> found = cupomRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(cupom.getCode(), found.get().getCode());
        verify(cupomRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar empty quando cupom não encontrado por ID")
    void shouldReturnEmptyWhenCupomNotFoundById() {
        when(cupomRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Cupom> found = cupomRepository.findById(999L);

        assertFalse(found.isPresent());
        verify(cupomRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve encontrar cupom por código")
    void shouldFindCupomByCode() {
        when(cupomRepository.findByCode("ABC123")).thenReturn(Optional.of(cupom));

        Optional<Cupom> found = cupomRepository.findByCode("ABC123");

        assertTrue(found.isPresent());
        assertEquals("ABC123", found.get().getCode());
        verify(cupomRepository, times(1)).findByCode("ABC123");
    }

    @Test
    @DisplayName("Deve retornar empty quando cupom não encontrado por código")
    void shouldReturnEmptyWhenCupomNotFoundByCode() {
        when(cupomRepository.findByCode("XYZ999")).thenReturn(Optional.empty());

        Optional<Cupom> found = cupomRepository.findByCode("XYZ999");

        assertFalse(found.isPresent());
        verify(cupomRepository, times(1)).findByCode("XYZ999");
    }

    @Test
    @DisplayName("Deve verificar se cupom existe por código")
    void shouldCheckIfCupomExistsByCode() {
        when(cupomRepository.existsByCode("ABC123")).thenReturn(true);

        boolean exists = cupomRepository.existsByCode("ABC123");

        assertTrue(exists);
        verify(cupomRepository, times(1)).existsByCode("ABC123");
    }

    @Test
    @DisplayName("Deve retornar false quando cupom não existe por código")
    void shouldReturnFalseWhenCupomDoesNotExistByCode() {
        when(cupomRepository.existsByCode("NONEXISTENT")).thenReturn(false);

        boolean exists = cupomRepository.existsByCode("NONEXISTENT");

        assertFalse(exists);
        verify(cupomRepository, times(1)).existsByCode("NONEXISTENT");
    }

    @Test
    @DisplayName("Deve verificar se cupom existe por código com lock")
    void shouldCheckIfCupomExistsByCodeWithLock() {
        when(cupomRepository.existsByCode("ABC123")).thenReturn(true);

        boolean exists = cupomRepository.existsByCode("ABC123");

        assertTrue(exists);
        verify(cupomRepository, times(1)).existsByCode("ABC123");
    }

    @Test
    @DisplayName("Deve retornar false quando cupom não existe por código com lock")
    void shouldReturnFalseWhenCupomDoesNotExistByCodeWithLock() {
        when(cupomRepository.existsByCode("NONEXISTENT")).thenReturn(false);

        boolean exists = cupomRepository.existsByCode("NONEXISTENT");

        assertFalse(exists);
        verify(cupomRepository, times(1)).existsByCode("NONEXISTENT");
    }

    @Test
    @DisplayName("Deve encontrar cupom deletado por ID")
    void shouldFindDeletedCupomById() {
        cupom.delete();
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));

        Optional<Cupom> found = cupomRepository.findById(1L);

        assertTrue(found.isPresent());
        assertTrue(found.get().isDeleted());
        verify(cupomRepository, times(1)).findById(1L);
    }


}
