package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validator.ValidateGroups;

@Validated
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") @Positive Long requestId) {
        log.info("Gateway sent request [getRequestById]: requestId={}", requestId);
        return itemRequestClient.getItemByID(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId) {
        log.info("Gateway sent request [getRequests]: userId={}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId) {
        log.info("Gateway sent request [getRequestsAll]: userId={}", userId);
        return itemRequestClient.getAll(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                              @RequestBody @Validated(ValidateGroups.OnCreate.class) ItemRequestDto dto) {
        log.info("Gateway sent request [postRequest]: userId={}, req={}", userId, dto);
        return itemRequestClient.postItemReq(userId, dto);
    }
}
