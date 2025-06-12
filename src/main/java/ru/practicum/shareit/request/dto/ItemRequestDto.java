package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDate;

public record ItemRequestDto(

        Long id,

        Long requester,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        String description,

        LocalDate created
) {
}
