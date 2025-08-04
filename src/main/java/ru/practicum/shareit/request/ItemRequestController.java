package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable("requestId") @Positive Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests() {
        return null;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsAll(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return itemRequestService.getAllExceptUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto postRequest(@RequestBody ItemRequestDto dto) {
        return itemRequestService.addItemRequest(dto);
    }


}
