package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface IItemRequestService {
    
    ItemRequestDto getItemRequestById(Long id);

    List<ItemRequestDto> getAllExceptUserId(Long id);

    List<ItemRequestDto> getAll();

    ItemRequestDto addItemRequest(ItemRequestDto ItemRequestDto);
}
