package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record BookingDto(

        Long id,

        @NotNull(groups = ValidateGroups.OnCreate.class)
        @FutureOrPresent(groups = {ValidateGroups.OnCreate.class, ValidateGroups.OnPatch.class})
        LocalDateTime start,

        @NotNull(groups = ValidateGroups.OnCreate.class)
        @Future(groups = {ValidateGroups.OnCreate.class, ValidateGroups.OnPatch.class})
        LocalDateTime end,

        Long itemId,

        ItemDto item,

        UserDto booker,

        BookingState status
) {
}
