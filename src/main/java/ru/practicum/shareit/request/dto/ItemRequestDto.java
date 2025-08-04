package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDateTime;

@Builder
public record ItemRequestDto(

        Long id,

        String title,

        Long requestId,

        Long itemId,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        String description,

        LocalDateTime created
) {
}
