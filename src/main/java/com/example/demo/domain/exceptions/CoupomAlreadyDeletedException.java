package com.example.demo.domain.exceptions;

public class CoupomAlreadyDeletedException extends RuntimeException {
    public CoupomAlreadyDeletedException(String message) {
        super(message);
    }
}
