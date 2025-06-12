package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

public record BookingDto(

        Long id,

        LocalDate start,

        LocalDate end,

        Item item,

        User booker,

        BookingStatus status
) {
}
