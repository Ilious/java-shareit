package ru.practicum.shareit.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String field) {
        super("Field [%s] is invalid".formatted(field));
    }
}
