package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

/**
 * TODO Sprint add-controllers.
 */
@Builder
public record ItemDto(

        Long id,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        String name,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        String description,

        @JsonProperty("available")
        @NotNull(groups = ValidateGroups.OnCreate.class)
        Boolean isAvailable,

        UserDto owner
) {
}
