package com.example.demo.application;

import com.example.demo.application.usecases.FindCupomUseCase;
import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FindCupomUseCase Tests")
@ExtendWith(MockitoExtension.class)
class FindCupomUseCaseTest {

    @Mock
    private CupomRepository cupomRepository;

    private FindCupomUseCase findUseCase;

    private Cupom cupomFixture;

    @BeforeEach
    void setUp() {
        findUseCase = new FindCupomUseCase(cupomRepository);

        cupomFixture = new Cupom(
                "TEST01",
                "Cupom para teste",
                new BigDecimal("10.00"),
                LocalDateTime.now().plusDays(7),
                true
        );
        cupomFixture.setId(1L);
        cupomFixture.setVersion(1L);
    }

    @Nested
    @DisplayName("Casos de Sucesso")
    class SuccessCases {

        @Test
        @DisplayName("Deve encontrar cupom existente por ID")
        void shouldFindExistingCupomById() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));

            Optional<Cupom> result = findUseCase.execute(1L);

            assertTrue(result.isPresent());
            assertEquals(cupomFixture.getCode(), result.get().getCode());
            verify(cupomRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve retornar cupom com dados corretos")
        void shouldReturnCupomWithCorrectData() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));

            Optional<Cupom> result = findUseCase.execute(1L);

            assertTrue(result.isPresent());
            Cupom cupom = result.get();
            assertEquals("TEST01", cupom.getCode());
            assertEquals("Cupom para teste", cupom.getDescription());
            assertEquals(new BigDecimal("10.00"), cupom.getDiscountValue());
            assertTrue(cupom.isPublished());
        }

        @Test
        @DisplayName("Deve retornar cupom com todas as propriedades")
        void shouldReturnCupomWithAllProperties() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));

            Optional<Cupom> result = findUseCase.execute(1L);

            assertTrue(result.isPresent());
            Cupom cupom = result.get();
            assertNotNull(cupom.getId());
            assertNotNull(cupom.getCode());
            assertNotNull(cupom.getDescription());
            assertNotNull(cupom.getDiscountValue());
            assertNotNull(cupom.getExpirationDate());
            assertNotNull(cupom.getVersion());
        }

        @Test
        @DisplayName("Deve encontrar cupom deletado")
        void shouldFindDeletedCupom() {
            cupomFixture.delete();
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));

            Optional<Cupom> result = findUseCase.execute(1L);

            assertTrue(result.isPresent());
            assertTrue(result.get().isDeleted());
        }

        @Test
        @DisplayName("Deve encontrar cupom não publicado")
        void shouldFindUnpublishedCupom() {
            Cupom unpublishedCupom = new Cupom(
                    "UNPUB0",
                    "Cupom não publicado",
                    new BigDecimal("15.00"),
                    LocalDateTime.now().plusDays(7),
                    false
            );
            unpublishedCupom.setId(2L);

            when(cupomRepository.findById(2L)).thenReturn(Optional.of(unpublishedCupom));

            Optional<Cupom> result = findUseCase.execute(2L);

            assertTrue(result.isPresent());
            assertFalse(result.get().isPublished());
        }

        @Test
        @DisplayName("Deve encontrar cupom com valores grandes")
        void shouldFindCupomWithLargeValues() {
            Cupom largeCupom = new Cupom(
                    "LARGE0",
                    "Desconto grande",
                    new BigDecimal("99.99"),
                    LocalDateTime.now().plusYears(10),
                    true
            );
            largeCupom.setId(3L);

            when(cupomRepository.findById(3L)).thenReturn(Optional.of(largeCupom));

            Optional<Cupom> result = findUseCase.execute(3L);

            assertTrue(result.isPresent());
            assertEquals(new BigDecimal("99.99"), result.get().getDiscountValue());
        }
    }

    @Nested
    @DisplayName("Casos de Cupom Não Encontrado")
    class CupomNotFoundCases {

        @Test
        @DisplayName("Deve retornar empty quando cupom não existe")
        void shouldReturnEmptyWhenCupomDoesNotExist() {
            when(cupomRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Cupom> result = findUseCase.execute(999L);

            assertFalse(result.isPresent());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio para ID inexistente")
        void shouldReturnEmptyOptionalForNonExistentId() {
            when(cupomRepository.findById(0L)).thenReturn(Optional.empty());

            Optional<Cupom> result = findUseCase.execute(0L);

            assertTrue(result.isEmpty());
        }

    }

    @Nested
    @DisplayName("Testes de Interação com Repository")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("Deve chamar repository.findById com ID correto")
        void shouldCallRepositoryWithCorrectId() {
            when(cupomRepository.findById(5L)).thenReturn(Optional.of(cupomFixture));

            findUseCase.execute(5L);

            verify(cupomRepository, times(1)).findById(5L);
        }

        @Test
        @DisplayName("Deve chamar repository uma única vez")
        void shouldCallRepositoryExactlyOnce() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));

            findUseCase.execute(1L);

            verify(cupomRepository, times(1)).findById(1L);
            verifyNoMoreInteractions(cupomRepository);
        }

    }




}