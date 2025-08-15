package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Builder(toBuilder = true)
public record ItemDto(

        Long id,

        String name,

        String description,

        @JsonProperty("available")
        Boolean isAvailable,

        Long requestId,

        UserDto owner,

        List<CommentDto> comments,

        BookingDto lastBooking,

        BookingDto nextBooking
) {
}
