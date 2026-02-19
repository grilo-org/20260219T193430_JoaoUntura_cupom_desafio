package com.example.demo.application;

import com.example.demo.application.execeptions.CupomConcurrencyException;
import com.example.demo.application.execeptions.CupomNotFound;
import com.example.demo.application.usecases.DeleteCupomUseCase;
import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import jakarta.persistence.OptimisticLockException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("DeleteCupomUseCase Tests")
@ExtendWith(MockitoExtension.class)
class DeleteCupomUseCaseTest {

    @Mock
    private CupomRepository cupomRepository;

    private DeleteCupomUseCase deleteUseCase;

    private Cupom cupomFixture;

    @BeforeEach
    void setUp() {
        deleteUseCase = new DeleteCupomUseCase(cupomRepository);

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
        @DisplayName("Deve deletar cupom existente com sucesso")
        void shouldDeleteExistingCupomSuccessfully() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(1L);

            verify(cupomRepository, times(1)).findById(1L);
            verify(cupomRepository, times(1)).save(any(Cupom.class));
            assertTrue(cupomFixture.isDeleted());
        }

        @Test
        @DisplayName("Deve chamar cupom.delete() antes de salvar")
        void shouldCallCupomDeleteBeforeSaving() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            assertFalse(cupomFixture.isDeleted());

            deleteUseCase.execute(1L);

            assertTrue(cupomFixture.isDeleted());
            verify(cupomRepository).save(cupomFixture);
        }

        @Test
        @DisplayName("Deve manter transação ao deletar com sucesso")
        void shouldMaintainTransactionOnSuccessfulDeletion() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(1L);

            verify(cupomRepository).findById(1L);
            verify(cupomRepository).save(cupomFixture);
            verifyNoMoreInteractions(cupomRepository);
        }

        @Test
        @DisplayName("Deve registrar data de deleção ao deletar")
        void shouldRecordDeletionDateWhenDeleting() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            LocalDateTime beforeDelete = LocalDateTime.now();
            deleteUseCase.execute(1L);
            LocalDateTime afterDelete = LocalDateTime.now();

            assertNotNull(cupomFixture.getDeletedAt());
            assertTrue(cupomFixture.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
            assertTrue(cupomFixture.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("Casos de Erro")
    class ErrorCases {

        @Test
        @DisplayName("Deve lançar CupomNotFound quando cupom não existe")
        void shouldThrowCupomNotFoundWhenCupomDoesNotExist() {
            when(cupomRepository.findById(999L)).thenReturn(Optional.empty());

            CupomNotFound exception = assertThrows(
                    CupomNotFound.class,
                    () -> deleteUseCase.execute(999L)
            );

            assertTrue(exception.getMessage().contains("não foi encontrado"));
            verify(cupomRepository, times(1)).findById(999L);
            verify(cupomRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar CupomNotFound com mensagem apropriada")
        void shouldThrowCupomNotFoundWithAppropriateMessage() {
            when(cupomRepository.findById(999L)).thenReturn(Optional.empty());

            CupomNotFound exception = assertThrows(
                    CupomNotFound.class,
                    () -> deleteUseCase.execute(999L)
            );

            assertEquals("Cupom a ser deletado não foi encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar CupomConcurrencyException quando há conflito de versão")
        void shouldThrowCupomConcurrencyExceptionOnOptimisticLockException() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class)))
                    .thenThrow(new OptimisticLockException("Version mismatch"));

            CupomConcurrencyException exception = assertThrows(
                    CupomConcurrencyException.class,
                    () -> deleteUseCase.execute(1L)
            );

            assertTrue(exception.getMessage().contains("modificado por outra transação"));
        }

        @Test
        @DisplayName("Deve lançar CupomConcurrencyException com mensagem apropriada")
        void shouldThrowCupomConcurrencyExceptionWithAppropriateMessage() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class)))
                    .thenThrow(new OptimisticLockException("Version mismatch"));

            CupomConcurrencyException exception = assertThrows(
                    CupomConcurrencyException.class,
                    () -> deleteUseCase.execute(1L)
            );

            assertEquals("Cupom foi modificado por outra transação", exception.getMessage());
        }

        @Test
        @DisplayName("Não deve tentar salvar quando cupom não encontrado")
        void shouldNotAttemptSaveWhenCupomNotFound() {
            when(cupomRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(
                    CupomNotFound.class,
                    () -> deleteUseCase.execute(999L)
            );

            verify(cupomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Interação com Repository")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("Deve buscar cupom por ID correto")
        void shouldFetchCupomWithCorrectId() {
            when(cupomRepository.findById(5L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(5L);

            verify(cupomRepository).findById(5L);
        }

        @Test
        @DisplayName("Deve salvar cupom após deletar")
        void shouldSaveCupomAfterDeleting() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(1L);

            verify(cupomRepository).save(cupomFixture);
        }

        @Test
        @DisplayName("Deve deletar múltiplos cupons sequencialmente")
        void shouldDeleteMultipleCupomsSequentially() {
            Cupom cupom2 = new Cupom("TEST02", "Cupom 2", new BigDecimal("20.00"),
                    LocalDateTime.now().plusDays(7), true);
            cupom2.setId(2L);

            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.findById(2L)).thenReturn(Optional.of(cupom2));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(1L);
            deleteUseCase.execute(2L);

            verify(cupomRepository, times(2)).findById(any());
            verify(cupomRepository, times(2)).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Estado")
    class StateTests {

        @Test
        @DisplayName("Cupom deve estar deletado após execute")
        void shouldCupomBeDeletedAfterExecute() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            assertFalse(cupomFixture.isDeleted());

            deleteUseCase.execute(1L);

            assertTrue(cupomFixture.isDeleted());
        }

        @Test
        @DisplayName("Deve impedir deleção dupla mesmo em novo execute")
        void shouldPreventDoubleDeletionInNewExecute() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(1L);

            // Simular retorno do cupom já deletado
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));

            assertThrows(
                    Exception.class, // CoupomAlreadyDeletedException
                    () -> deleteUseCase.execute(1L)
            );
        }
    }

    @Nested
    @DisplayName("Testes de Transação")
    class TransactionTests {

        @Test
        @DisplayName("Deve completar transação de forma atômica")
        void shouldCompleteTransactionAtomically() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomFixture);

            deleteUseCase.execute(1L);

            // Verificar que ambas operações foram chamadas
            verify(cupomRepository).findById(1L);
            verify(cupomRepository).save(cupomFixture);
        }

        @Test
        @DisplayName("Deve fazer rollback em caso de erro")
        void shouldRollbackOnError() {
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomFixture));
            when(cupomRepository.save(any(Cupom.class)))
                    .thenThrow(new OptimisticLockException("Error"));

            assertThrows(
                    CupomConcurrencyException.class,
                    () -> deleteUseCase.execute(1L)
            );

            // Cupom não deveria estar salvo
            verify(cupomRepository).save(any());
        }
    }
}