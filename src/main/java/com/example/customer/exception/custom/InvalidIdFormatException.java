package com.example.customer.exception.custom;

public class InvalidIdFormatException extends RuntimeException {
    public InvalidIdFormatException(String message) {
        super(message);
    }
}

