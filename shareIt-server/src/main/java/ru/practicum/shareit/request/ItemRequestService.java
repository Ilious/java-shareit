package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.interfaces.IItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepo;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ItemRequestService implements IItemRequestService {

    private final ItemRequestRepo itemRequestRepo;

    private final UserRepo userRepo;

    private final ItemRepo itemRepo;

    @Override
    public ItemRequestDto getItemRequestById(Long id) {
        List<ItemDto> items = itemRepo.findAllByRequestId(id)
                .stream()
                .map(ItemMapper::toDto)
                .toList();

        return ItemRequestMapper.toDto(itemRequestRepo.getItemRequestById(id), items);
    }

    @Override
    public List<ItemRequestDto> getRequestsAll(Long userId) {
        return getAllWithSuggests(userId, true);
    }

    @Override
    public List<ItemRequestDto> getRequestExceptUser(Long id) {
        return getAllWithSuggests(id, false);
    }

    private List<ItemRequestDto> getAllWithSuggests(Long id, boolean exceptUser) {
        List<ItemRequest> requests = exceptUser
                ? itemRequestRepo.findAllByRequesterIdNotOrderByCreatedDesc(id) :
                itemRequestRepo.findAllByRequesterIdOrderByCreatedDesc(id);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> relatedItems = itemRepo.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = relatedItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    List<ItemDto> itemDtos = items.stream()
                            .map(ItemMapper::toDto)
                            .toList();

                    return ItemRequestMapper.toDto(request, itemDtos);
                })
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto dto) {
        User user = userRepo.getUserById(userId);

        ItemRequest entity = ItemRequestMapper.toEntity(dto, user);
        ItemRequest saved = itemRequestRepo.save(entity);

        return ItemRequestMapper.toDto(saved);
    }
}
