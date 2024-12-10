package com.example.customer.utils;

import java.util.Base64;

public class CustomerUtils {
    // Encodes the input text into a secret
    public static String encodeToBase64(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be null or empty");
        }
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    //decodes the encoded text into plain text
    public static String decodeFromBase64(String encodedText) {
        if (encodedText == null || encodedText.isEmpty()) {
            throw new IllegalArgumentException("Encoded text cannot be null or empty");
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        return new String(decodedBytes);
    }

}
