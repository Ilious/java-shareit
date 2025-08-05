package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.ValidateGroups;

@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    private final static String HEADER_USER_ID =  "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long id) {
        log.info("Gateway sent request [getItemById]: id={}", id);
        return itemClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId) {
        log.info("Gateway sent request [getAllByUserId]: userId={}", userId);
        return itemClient.getByAllUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> postItem(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                            @Validated(ValidateGroups.OnCreate.class) @RequestBody ItemDto dto) {
        log.info("Gateway sent request [postItem]: userId={}, itemName={}", userId, dto.name());
        return itemClient.postItem(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                             @PathVariable Long id, @RequestBody ItemDto dto) {
        log.info("Gateway sent request [patchItem]: userId={}, name={}", userId, dto.name());
        return itemClient.patchItem(id, userId, dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam String text) {
        log.info("Gateway sent request [searchByText]: text={}", text);
        return itemClient.searchByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(name = HEADER_USER_ID) @Positive Long userId,
                                  @PathVariable @Positive Long itemId, @RequestBody CommentDto dto) {
        log.info("Gateway sent request [postComment]: userId={}, itemId={}", userId, itemId);
        return itemClient.postComment(userId, itemId, dto);
    }
}
