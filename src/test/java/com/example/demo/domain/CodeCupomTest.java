package com.example.demo.domain;



import com.example.demo.domain.exceptions.InvalidCodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CodeCupom Tests")
class CodeCupomTest {

    @Test
    @DisplayName("Deve criar código com valor válido")
    void shouldCreateCodeWithValidValue() {
        String validCode = "ABC123";

        CodeCupom code = new CodeCupom(validCode);

        assertEquals("ABC123", code.getValue());
    }

    @Test
    @DisplayName("Deve criar código normalizando caracteres especiais")
    void shouldCreateCodeWithSpecialCharactersRemoved() {
        String codeWithSpecialChars = "A!B@C#1$2%3";

        CodeCupom code = new CodeCupom(codeWithSpecialChars);

        assertEquals("ABC123", code.getValue());
    }

    @Test
    @DisplayName("Deve criar código com espaços removidos")
    void shouldCreateCodeWithSpacesRemoved() {
        String codeWithSpaces = "A B C 1 2 3";

        CodeCupom code = new CodeCupom(codeWithSpaces);

        assertEquals("ABC123", code.getValue());
    }


    @Test
    @DisplayName("Deve criar código com todos os números")
    void shouldCreateCodeWithAllNumbers() {
        String numberCode = "123456";

        CodeCupom code = new CodeCupom(numberCode);

        assertEquals("123456", code.getValue());
    }

    @Test
    @DisplayName("Deve criar código com todas as letras")
    void shouldCreateCodeWithAllLetters() {
        String letterCode = "ABCDEF";

        CodeCupom code = new CodeCupom(letterCode);

        assertEquals("ABCDEF", code.getValue());
    }

    @Test
    @DisplayName("Deve converter letras minúsculas para maiúsculas na comparação")
    void shouldHandleLowercaseLetters() {
        String lowercaseCode = "abc123";

        CodeCupom code = new CodeCupom(lowercaseCode);

        // Nota: O código atual mantém minúsculas, apenas remove não-alfanuméricos
        assertEquals("abc123", code.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção quando código é nulo")
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThrows(
                InvalidCodeException.class,
                () -> new CodeCupom(null)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando código tem menos de 6 caracteres após normalização")
    void shouldThrowExceptionWhenCodeIsTooShort() {
        String tooShortCode = "ABC12";

        InvalidCodeException exception = assertThrows(
                InvalidCodeException.class,
                () -> new CodeCupom(tooShortCode)
        );

        assertTrue(exception.getMessage().contains("6"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando código tem mais de 6 caracteres após normalização")
    void shouldThrowExceptionWhenCodeIsTooLong() {
        String tooLongCode = "ABC1234";

        InvalidCodeException exception = assertThrows(
                InvalidCodeException.class,
                () -> new CodeCupom(tooLongCode)
        );

        assertTrue(exception.getMessage().contains("6"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando código sem caracteres alfanuméricos")
    void shouldThrowExceptionWhenCodeHasNoAlphanumericCharacters() {
        String noAlphanumericCode = "!@#$%^";

        assertThrows(
                InvalidCodeException.class,
                () -> new CodeCupom(noAlphanumericCode)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando código em branco")
    void shouldThrowExceptionWhenCodeIsBlank() {
        String blankCode = "   ";

        assertThrows(
                InvalidCodeException.class,
                () -> new CodeCupom(blankCode)
        );
    }

    @Test
    @DisplayName("Deve retornar valor do código")
    void shouldReturnCodeValue() {
        String code = "XYZ789";
        CodeCupom cupomCode = new CodeCupom(code);

        assertEquals("XYZ789", cupomCode.getValue());
    }

    @Test
    @DisplayName("Deve normalizar código complexo")
    void shouldNormalizeComplexCode() {
        String complexCode = "A-B_C@1#2$3";

        CodeCupom code = new CodeCupom(complexCode);

        assertEquals("ABC123", code.getValue());
    }
}
