package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import ru.practicum.shareit.validator.ValidateGroups;

@Builder
public record UserDto(

        Long id,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        String name,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        @Email(groups = {ValidateGroups.OnCreate.class, ValidateGroups.OnPatch.class})
        String email
) {
}
