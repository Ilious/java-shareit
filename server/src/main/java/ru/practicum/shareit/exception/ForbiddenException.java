package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {

    private final Long userId;

    public ForbiddenException(Long userId) {
        super("Forbidden by for user: userId {%d}".formatted(userId));
        this.userId = userId;
    }
}
