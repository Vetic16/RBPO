package ru.mtuci.rbpo_2024_praktika.exception;

public class JwtTokenException extends RuntimeException {
    public JwtTokenException(String message) {
        super(message);
    }
}