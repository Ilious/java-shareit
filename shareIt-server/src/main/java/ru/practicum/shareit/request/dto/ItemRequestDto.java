package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ItemRequestDto(

        Long id,

        String title,

        String description,

        LocalDateTime created,

        UserDto requester,

        List<ItemDto> items
) {
}