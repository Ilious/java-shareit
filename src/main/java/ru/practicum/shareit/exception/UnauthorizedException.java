package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final Long userId;

    public UnauthorizedException(Long userId) {
        super("Unauthorized by id {%d}".formatted(userId));
        this.userId = userId;
    }
}
