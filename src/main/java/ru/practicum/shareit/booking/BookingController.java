package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.interfaces.IBookingService;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.validator.ValidateGroups;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService bookingService;

    @GetMapping("/{id}")
    public BookingDto getById(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                              @PathVariable @Positive Long id) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                   @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        return bookingService.getAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByUserItems(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                   @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        return bookingService.getAllBookingByOwnerId(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
            @RequestBody @Validated(ValidateGroups.OnCreate.class) BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public BookingDto patchBooking(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                   @RequestParam(defaultValue = "false") Boolean approved,
                                   @PathVariable @Positive Long id) {
        return bookingService.patchBooking(userId, id, approved);
    }
}
