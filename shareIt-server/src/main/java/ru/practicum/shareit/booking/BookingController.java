package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.interfaces.IBookingService;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService bookingService;
    
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public BookingDto getById(@RequestHeader(name = HEADER_USER_ID) Long userId,
                              @PathVariable Long id) {
        log.info("Server received request [getById]: userId={}, id={}", userId, id);
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader(name = HEADER_USER_ID) Long userId,
                                   @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("Server received request [getAllBookingsByUser]: userId={}, state={}", userId, state);
        return bookingService.getAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByUserItems(@RequestHeader(name = HEADER_USER_ID) Long userId,
                                   @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("Server received request [getAllBookingsByUserItems]: userId={}, state={}", userId, state);
        return bookingService.getAllBookingByOwnerId(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestBody BookingDto bookingDto) {
        log.info("Server received request [createBooking]: userId={}, bookingSt = {}", userId, bookingDto.start());
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public BookingDto patchBooking(@RequestHeader(name = HEADER_USER_ID) Long userId,
                                   @RequestParam(defaultValue = "false") Boolean approved,
                                   @PathVariable Long id) {
        log.info("Server received request [patchBooking]: userId={}, approved={}", userId, approved);
        return bookingService.patchBooking(userId, id, approved);
    }
}
