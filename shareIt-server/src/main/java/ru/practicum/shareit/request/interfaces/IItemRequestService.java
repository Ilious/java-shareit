package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface IItemRequestService {

    ItemRequestDto getItemRequestById(Long id);

    List<ItemRequestDto> getRequestsAll(Long userId);

    List<ItemRequestDto> getRequestExceptUser(Long id);

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto ItemRequestDto);
}
