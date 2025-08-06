package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record BookingDto(

        Long id,

        LocalDateTime start,

        LocalDateTime end,

        Long itemId,

        ItemDto item,

        UserDto booker,

        BookingStatus status
) {
}
