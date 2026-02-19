package com.example.demo.domain.exceptions;

public class InvalidCupomException extends RuntimeException {
    public InvalidCupomException(String message) {
        super(message);
    }
}
