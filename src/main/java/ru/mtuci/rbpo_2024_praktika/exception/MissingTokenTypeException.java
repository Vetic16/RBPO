package ru.mtuci.rbpo_2024_praktika.exception;

public class MissingTokenTypeException extends RuntimeException {
    public MissingTokenTypeException(String message) {
        super(message);
    }
}