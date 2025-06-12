package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.IItemService;
import ru.practicum.shareit.validator.ValidateGroups;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final IItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable @Positive Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto postItem(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
            @Validated(ValidateGroups.OnCreate.class) @RequestBody ItemDto dto) {
        return itemService.addItem(userId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
            @PathVariable Long id, @RequestBody ItemDto dto) {
        return itemService.patchItem(id, dto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        return itemService.searchByTextQuery(text);
    }
}
