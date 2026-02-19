package com.example.demo.application.execeptions;

public class CodeAlreadyExists extends RuntimeException {
    public CodeAlreadyExists(String message) {
        super(message);
    }
}
