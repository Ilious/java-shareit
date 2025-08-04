package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface IBookingService {

    List<BookingDto> getAllBookingByUserId(Long userId, BookingState state);

    List<BookingDto> getAllBookingByOwnerId(Long userId, BookingState state);

    BookingDto getBookingById(Long id, Long userId);

    BookingDto addBooking(Long userId, BookingDto dto);

    BookingDto patchBooking(Long userId, Long bookingId, Boolean status);
}
