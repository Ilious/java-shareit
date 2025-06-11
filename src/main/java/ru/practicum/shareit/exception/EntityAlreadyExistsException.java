package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {

    private final String entity;

    private final String field;

    private final Object value;

    public EntityAlreadyExistsException(String entity, String field, Object value) {
        super("Entity {%s} already exists by {%s}: {%s}".formatted(entity, field, value));
        this.entity = entity;
        this.field = field;
        this.value = value;
    }
}
