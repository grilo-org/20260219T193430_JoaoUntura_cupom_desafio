package com.example.demo.application.execeptions;

public class CupomConcurrencyException extends RuntimeException {
    public CupomConcurrencyException(String message) {
        super(message);
    }
}
