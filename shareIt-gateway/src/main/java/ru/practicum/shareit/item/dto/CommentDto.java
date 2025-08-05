package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
    Long id,

    @NotBlank(groups = ValidateGroups.OnCreate.class)
    String text,

    @NotBlank(groups = ValidateGroups.OnCreate.class)
    String authorName,

    @NotNull(groups = ValidateGroups.OnCreate.class)
    @PastOrPresent(groups = {ValidateGroups.OnCreate.class, ValidateGroups.OnPatch.class})
    LocalDateTime created
) {
}
