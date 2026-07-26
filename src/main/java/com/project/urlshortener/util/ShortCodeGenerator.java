package com.project.urlshortener.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Short code length must be positive");
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(ALPHABET[secureRandom.nextInt(ALPHABET.length)]);
        }
        return code.toString().toLowerCase();
    }
}
