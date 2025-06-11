package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entity;

    private final String field;

    private final Object value;

    public EntityNotFoundException(String entity, String field, Object value) {
        super("Entity {%s} not found by {%s}: {%s}".formatted(entity, field, value));
        this.entity = entity;
        this.field = field;
        this.value = value;
    }
}
