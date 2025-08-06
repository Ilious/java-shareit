package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable("requestId") Long requestId) {
        log.info("Server received request [getRequestById]: requestId={}", requestId);
        return itemRequestService.getItemRequestById(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(name = HEADER_USER_ID) Long userId) {
        log.info("Server received request [getRequests]: userId={}", userId);
        return itemRequestService.getRequestExceptUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsAll(@RequestHeader(name = HEADER_USER_ID) Long userId) {
        log.info("Server received request [getRequestsAll]: userId={}", userId);
        return itemRequestService.getRequestsAll(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader(name = HEADER_USER_ID) Long userId,
                                      @RequestBody ItemRequestDto dto) {
        log.info("Server received request [postRequest]: userId={}, req={}", userId, dto);
        return itemRequestService.addItemRequest(userId, dto);
    }
}
