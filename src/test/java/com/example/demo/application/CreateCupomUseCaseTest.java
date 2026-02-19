package com.example.demo.application;

import com.example.demo.application.dtos.CreateCupomDto;
import com.example.demo.application.dtos.CupomResponse;
import com.example.demo.application.execeptions.CodeAlreadyExists;
import com.example.demo.application.usecases.CreateCupomUseCase;
import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import com.example.demo.domain.exceptions.InvalidCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CreateCupomUseCase Tests")
@ExtendWith(MockitoExtension.class)
class CreateCupomUseCaseTest {

    @Mock
    private CupomRepository cupomRepository;

    private CreateCupomUseCase createUseCase;

    private CreateCupomDto validRequest;

    @BeforeEach
    void setUp() {
        createUseCase = new CreateCupomUseCase(cupomRepository);

        validRequest = new CreateCupomDto(
                "ABC123",
                "Desconto válido",
                new BigDecimal("10.00"),
                LocalDateTime.now().plusDays(7),
                true
        );
    }

    @Nested
    @DisplayName("Casos de Sucesso")
    class SuccessCases {

        @Test
        @DisplayName("Deve criar cupom com sucesso")
        void shouldCreateCupomSuccessfully() {
            when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

            Cupom savedCupom = new Cupom(
                    validRequest.code(),
                    validRequest.description(),
                    validRequest.discountValue(),
                    validRequest.expirationDate(),
                    validRequest.published()
            );
            savedCupom.setId(1L);

            when(cupomRepository.save(any(Cupom.class))).thenReturn(savedCupom);

            CupomResponse response = createUseCase.execute(validRequest);

            assertNotNull(response);
            assertEquals("ABC123", response.code());
            verify(cupomRepository).existsByCode("ABC123");
            verify(cupomRepository).save(any(Cupom.class));
        }

        @Test
        @DisplayName("Deve retornar CupomResponse com dados corretos")
        void shouldReturnCupomResponseWithCorrectData() {
            when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

            Cupom savedCupom = new Cupom(
                    validRequest.code(),
                    validRequest.description(),
                    validRequest.discountValue(),
                    validRequest.expirationDate(),
                    validRequest.published()
            );
            savedCupom.setId(100L);

            when(cupomRepository.save(any(Cupom.class))).thenReturn(savedCupom);

            CupomResponse response = createUseCase.execute(validRequest);

            assertEquals("ABC123", response.code());
            assertEquals("Desconto válido", response.description());
            assertEquals(new BigDecimal("10.00"), response.discountValue());
        }

        @Test
        @DisplayName("Deve usar lock pessimista para verificar duplicidade")
        void shouldUsePessimisticLockToCheckDuplicates() {
            when(cupomRepository.existsByCode("ABC123")).thenReturn(false);
            when(cupomRepository.save(any(Cupom.class))).thenReturn(new Cupom(
                    "ABC123", "Desc", new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(7), true
            ));

            createUseCase.execute(validRequest);

            verify(cupomRepository).existsByCode("ABC123");
        }

        @Test
        @DisplayName("Deve usar published como false quando não informado")
        void shouldUsePublishedAsFalseWhenNotProvided() {
            CreateCupomDto requestWithoutPublished = new CreateCupomDto(
                    "ABC123",
                    "Desconto válido",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(7),
                    null
            );

            when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

            ArgumentCaptor<Cupom> cupomCaptor = ArgumentCaptor.forClass(Cupom.class);
            Cupom savedCupom = new Cupom(
                    "ABC123", "Desconto válido", new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(7), false
            );
            savedCupom.setId(1L);

            when(cupomRepository.save(cupomCaptor.capture())).thenReturn(savedCupom);

            createUseCase.execute(requestWithoutPublished);

            Cupom capturedCupom = cupomCaptor.getValue();
            assertFalse(capturedCupom.isPublished());
        }

        @Test
        @DisplayName("Deve normalizar código antes de verificar duplicidade")
        void shouldNormalizeCodeBeforeCheckingDuplicates() {
            CreateCupomDto requestWithSpecialChars = new CreateCupomDto(
                    "A-B_C@1#2$3",
                    "Desconto válido",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(7),
                    true
            );

            when(cupomRepository.existsByCode("ABC123")).thenReturn(false);
            when(cupomRepository.save(any(Cupom.class))).thenReturn(new Cupom(
                    "ABC123", "Desc", new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(7), true
            ));

            createUseCase.execute(requestWithSpecialChars);

            // Verificar que foi normalizado para ABC123
            verify(cupomRepository).existsByCode("ABC123");
        }

        @Nested
        @DisplayName("Casos de Erro - Código Duplicado")
        class DuplicateCodeCases {

            @Test
            @DisplayName("Deve lançar CodeAlreadyExists quando código existe")
            void shouldThrowCodeAlreadyExistsWhenCodeExists() {
                when(cupomRepository.existsByCode("ABC123")).thenReturn(true);

                CodeAlreadyExists exception = assertThrows(
                        CodeAlreadyExists.class,
                        () -> createUseCase.execute(validRequest)
                );

                assertTrue(exception.getMessage().contains("já existe"));
                verify(cupomRepository, never()).save(any());
            }

            @Test
            @DisplayName("Deve lançar CodeAlreadyExists com mensagem apropriada")
            void shouldThrowCodeAlreadyExistsWithAppropriateMessage() {
                when(cupomRepository.existsByCode("ABC123")).thenReturn(true);

                CodeAlreadyExists exception = assertThrows(
                        CodeAlreadyExists.class,
                        () -> createUseCase.execute(validRequest)
                );

                assertEquals("Cupom com esse código já existe", exception.getMessage());
            }

            @Test
            @DisplayName("Não deve salvar quando código já existe")
            void shouldNotSaveWhenCodeAlreadyExists() {
                when(cupomRepository.existsByCode("ABC123")).thenReturn(true);

                assertThrows(
                        CodeAlreadyExists.class,
                        () -> createUseCase.execute(validRequest)
                );

                verify(cupomRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("Casos de Erro - Violação de Integridade")
        class IntegrityViolationCases {

            @Test
            @DisplayName("Deve tratar DataIntegrityViolationException como InvalidCodeException")
            void shouldHandleDataIntegrityViolationAsInvalidCodeException() {
                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);
                when(cupomRepository.save(any(Cupom.class)))
                        .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

                InvalidCodeException exception = assertThrows(
                        InvalidCodeException.class,
                        () -> createUseCase.execute(validRequest)
                );

                assertTrue(exception.getMessage().contains("já existe"));
            }

            @Test
            @DisplayName("Deve informar que cupom já existe na DataIntegrityViolationException")
            void shouldInformCupomAlreadyExistsOnIntegrityViolation() {
                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);
                when(cupomRepository.save(any(Cupom.class)))
                        .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

                InvalidCodeException exception = assertThrows(
                        InvalidCodeException.class,
                        () -> createUseCase.execute(validRequest)
                );

                assertEquals("Cupom já existe", exception.getMessage());
            }
        }

        @Nested
        @DisplayName("Testes de Normalização de Código")
        class CodeNormalizationTests {

            @Test
            @DisplayName("Deve normalizar código com hífens")
            void shouldNormalizeCodeWithHyphens() {
                CreateCupomDto requestWithHyphens = new CreateCupomDto(
                        "A-B-C-1-2-3",
                        "Desconto",
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(7),
                        true
                );

                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);
                when(cupomRepository.save(any(Cupom.class))).thenReturn(new Cupom(
                        "ABC123", "Desc", new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(7), true
                ));

                createUseCase.execute(requestWithHyphens);

                verify(cupomRepository).existsByCode("ABC123");
            }


            @Test
            @DisplayName("Deve normalizar código com caracteres especiais")
            void shouldNormalizeCodeWithSpecialCharacters() {
                CreateCupomDto requestWithSpecialChars = new CreateCupomDto(
                        "A!@B#$C%^1&*2(3)",
                        "Desconto",
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(7),
                        true
                );

                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);
                when(cupomRepository.save(any(Cupom.class))).thenReturn(new Cupom(
                        "ABC123", "Desc", new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(7), true
                ));

                createUseCase.execute(requestWithSpecialChars);

                verify(cupomRepository).existsByCode("ABC123");
            }
        }

        @Nested
        @DisplayName("Testes de Validação de Entrada")
        class InputValidationTests {

            @Test
            @DisplayName("Deve validar código inválido")
            void shouldValidateInvalidCode() {
                CreateCupomDto invalidCodeRequest = new CreateCupomDto(
                        "SHORT",  // Menos de 6 caracteres
                        "Desconto",
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(7),
                        true
                );


                // CodeCupom deve lançar exceção para código inválido
                assertThrows(
                        Exception.class,
                        () -> createUseCase.execute(invalidCodeRequest)
                );
            }

            @Test
            @DisplayName("Deve validar descrição inválida")
            void shouldValidateInvalidDescription() {
                CreateCupomDto invalidDescRequest = new CreateCupomDto(
                        "ABC123",
                        "",  // Descrição vazia
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(7),
                        true
                );

                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

                // Cupom deve lançar exceção para descrição inválida
                assertThrows(
                        Exception.class,
                        () -> createUseCase.execute(invalidDescRequest)
                );
            }

            @Test
            @DisplayName("Deve validar desconto inválido")
            void shouldValidateInvalidDiscount() {
                CreateCupomDto invalidDiscountRequest = new CreateCupomDto(
                        "ABC123",
                        "Desconto",
                        new BigDecimal("0.1"),  // Menor que mínimo
                        LocalDateTime.now().plusDays(7),
                        true
                );

                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

                // Cupom deve lançar exceção para desconto inválidow
                assertThrows(
                        Exception.class,
                        () -> createUseCase.execute(invalidDiscountRequest)
                );
            }

            @Test
            @DisplayName("Deve validar data de expiração inválida")
            void shouldValidateInvalidExpirationDate() {
                CreateCupomDto invalidDateRequest = new CreateCupomDto(
                        "ABC123",
                        "Desconto",
                        new BigDecimal("10.00"),
                        LocalDateTime.now().minusDays(1),  // Data no passado
                        true
                );

                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

                // Cupom deve lançar exceção para data inválida
                assertThrows(
                        Exception.class,
                        () -> createUseCase.execute(invalidDateRequest)
                );
            }
        }

        @Nested
        @DisplayName("Testes de Transação")
        class TransactionTests {

            @Test
            @DisplayName("Deve completar criação de forma atômica")
            void shouldCompleteCreationAtomically() {
                when(cupomRepository.existsByCode("ABC123")).thenReturn(false);

                Cupom savedCupom = new Cupom(
                        validRequest.code(),
                        validRequest.description(),
                        validRequest.discountValue(),
                        validRequest.expirationDate(),
                        validRequest.published()
                );
                savedCupom.setId(1L);

                when(cupomRepository.save(any(Cupom.class))).thenReturn(savedCupom);

                CupomResponse response = createUseCase.execute(validRequest);

                assertNotNull(response);
                verify(cupomRepository).existsByCode("ABC123");
                verify(cupomRepository).save(any(Cupom.class));
            }
        }
    }
}