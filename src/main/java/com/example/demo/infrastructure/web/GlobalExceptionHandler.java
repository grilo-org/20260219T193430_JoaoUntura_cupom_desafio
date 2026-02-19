package com.example.demo.infrastructure.web;


import com.example.demo.application.execeptions.CodeAlreadyExists;
import com.example.demo.application.execeptions.CupomConcurrencyException;
import com.example.demo.application.execeptions.CupomNotFound;
import com.example.demo.application.usecases.ErrorResponse;
import com.example.demo.domain.exceptions.CoupomAlreadyDeletedException;
import com.example.demo.domain.exceptions.InvalidCodeException;
import com.example.demo.domain.exceptions.InvalidCupomException;
import com.example.demo.domain.exceptions.InvalidDiscountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CodeAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleInvalidCode(CodeAlreadyExists ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ex.getMessage(), 400));
    }

    @ExceptionHandler(CupomConcurrencyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCode(CupomConcurrencyException ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ex.getMessage(), 400));
    }

    @ExceptionHandler(InvalidCupomException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCoupon(InvalidCupomException ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ex.getMessage(), 400));
    }

    @ExceptionHandler(InvalidDiscountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDiscont(InvalidDiscountException ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ex.getMessage(), 400));
    }

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCodeEx(InvalidCodeException ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ex.getMessage(), 400));
    }


    @ExceptionHandler(CoupomAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyDeleted(CoupomAlreadyDeletedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(ex.getMessage(), 422));
    }

    @ExceptionHandler(CupomNotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CupomNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), 404));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(message, 400));
    }
}