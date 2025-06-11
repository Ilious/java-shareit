package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface IItemService {

    ItemDto getItemById(Long id);

    List<ItemDto> getAllByUserId(Long userId);

    ItemDto addItem(Long userId, ItemDto dto);

    ItemDto patchItem(Long id, ItemDto dto, Long userId);

    List<ItemDto> searchByTextQuery(String query);
}
