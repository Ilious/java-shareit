package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.interfaces.IItemRequestService;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ItemRequestService implements IItemRequestService {

    private final ItemRequestRepo itemRequestRepo;

    @Override
    public ItemRequestDto getItemRequestById(Long id) {
        return ItemRequestMapper.toDto(itemRequestRepo.getItemRequestById(id));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return itemRequestRepo.findAllByOrderByCreatedDesc().stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllExceptUserId(Long id) {
        return itemRequestRepo.findAllByIdNotOrderByCreatedDesc(id).stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(ItemRequestDto ItemRequestDto) {
        ItemRequest entity = ItemRequestMapper.toEntity(ItemRequestDto);
        return ItemRequestMapper.toDto(itemRequestRepo.save(entity));
    }
}
