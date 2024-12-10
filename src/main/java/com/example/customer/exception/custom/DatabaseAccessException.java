package com.example.customer.exception.custom;

public class DatabaseAccessException extends RuntimeException {
    public DatabaseAccessException(String message) {
        super(message);
    }
}

