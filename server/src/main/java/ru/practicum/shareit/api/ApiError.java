package ru.practicum.shareit.api;

import lombok.Builder;

@Builder
public record ApiError(int code, String error) {
}
