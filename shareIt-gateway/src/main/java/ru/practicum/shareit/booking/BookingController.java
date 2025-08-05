package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.validator.ValidateGroups;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    private final static String HEADER_USER_ID =  "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                          @PathVariable @Positive Long id) {
        log.info("Gateway sent request [getById]: userId={}, id={}", userId, id);
        return bookingClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUser(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Gateway sent request [getAllBookingsByUser]: userId={}, state={}", userId, state);
        BookingState parsedState = BookingState.from(state)
                .orElseThrow(() -> new ValidationException("state"));
        return bookingClient.getByUser(userId, parsedState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByUserItems(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false)
                                                      String state) {
        log.info("Gateway send request [getAllBookingsByUserItems]: userId={}, state={}", userId, state);
        BookingState parsedState = BookingState.from(state)
                .orElseThrow(() -> new ValidationException("state"));
        return bookingClient.getByOwner(userId, parsedState);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                    @RequestBody @Validated(ValidateGroups.OnCreate.class) BookingDto bookingDto) {
        log.info("Gateway send request [createBooking]: userId={}, bookingSt = {}", userId, bookingDto.start());
        return bookingClient.postBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchBooking(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                   @RequestParam(defaultValue = "false") Boolean approved,
                                   @PathVariable @Positive Long id) {
        log.info("Gateway send request [patchBooking]: userId={}, approved={}", userId, approved);
        return bookingClient.patchBooking(userId, id, approved);
    }
}