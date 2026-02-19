package com.example.demo.application.execeptions;

public class CupomNotFound extends RuntimeException {
    public CupomNotFound(String message) {
        super(message);
    }
}
