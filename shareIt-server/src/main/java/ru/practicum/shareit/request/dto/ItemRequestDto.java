package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ItemRequestDto(

        Long id,

        String title,

        @NotBlank(groups = ValidateGroups.OnCreate.class)
        String description,

        LocalDateTime created,

        UserDto requester,

        List<ItemDto> items
) {
}