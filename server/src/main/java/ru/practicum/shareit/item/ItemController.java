package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.IItemService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final IItemService itemService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        log.info("Server received request [getItemById]: id={}", id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader(name = HEADER_USER_ID) Long userId) {
        log.info("Server received request [getAllByUserId]: userId={}", userId);
        return itemService.getAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto postItem(@RequestHeader(name = HEADER_USER_ID) Long userId, @RequestBody ItemDto dto) {
        log.info("Server received request [postItem]: userId={}, itemName={}", userId, dto.name());
        return itemService.addItem(userId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader(name = HEADER_USER_ID) Long userId,
                             @PathVariable Long id, @RequestBody ItemDto dto) {
        log.info("Server received request [patchItem]: userId={}, name={}", userId, dto.name());
        return itemService.patchItem(id, dto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        log.info("Server received request [searchByText]: text={}", text);
        return itemService.searchByTextQuery(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(name = HEADER_USER_ID) Long userId,
                                  @PathVariable Long itemId, @RequestBody CommentDto dto) {
        log.info("Server received request [postComment]: userId={}, itemId={}", userId, itemId);
        return itemService.addComment(userId, itemId, dto);
    }
}
