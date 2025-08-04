package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
public record BookingDto(

        Long id,

        LocalDateTime start,

        LocalDateTime end,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long itemId,

        ItemDto item,

        UserDto booker,

        BookingStatus status
) {
}
