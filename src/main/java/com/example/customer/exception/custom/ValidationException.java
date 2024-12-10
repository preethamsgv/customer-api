package com.example.customer.exception.custom;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

