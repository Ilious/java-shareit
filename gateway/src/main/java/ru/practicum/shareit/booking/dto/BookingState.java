package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,

    PAST,

    REJECTED,

    FUTURE,

    WAITING;

    public static Optional<BookingState> from(String value) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.name().equalsIgnoreCase(value)) {
                return Optional.of(bookingState);
            }
        }
        return Optional.empty();
    }
}
