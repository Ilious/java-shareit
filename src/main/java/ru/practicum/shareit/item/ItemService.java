package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.IItemRepo;
import ru.practicum.shareit.item.interfaces.IItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.IUserRepo;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    private final IUserRepo userRepo;

    private final IItemRepo itemRepo;

    @Override
    public ItemDto getItemById(Long id) {
        log.debug("ItemService.getItemById: id {}", id);
        return ItemMapper.toDto(getItem(id));
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        log.debug("ItemService.getAll: started");
        userRepo.getUserById(userId);
        List<Item> items = itemRepo.findAllByUserId(userId);
        if (items.isEmpty())
            throw new EntityNotFoundException("Items", "storage", "any");

        return items.stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        log.debug("ItemService.addItem: {}", dto);
        User userById = userRepo.getUserById(userId);
        Item entity = ItemMapper.toEntity(dto, userById);

        return ItemMapper.toDto(itemRepo.addItem(userId, entity));
    }

    @Override
    public ItemDto patchItem(Long id, ItemDto dto, Long userId) {
        log.debug("ItemService.patchItem: id {}, dto {}", id, dto);
        userRepo.getUserById(userId);
        Item item = getItem(id);
        ItemMapper.patchFields(item, dto);

        return ItemMapper.toDto(itemRepo.updItem(userId, item));
    }

    @Override
    public List<ItemDto> searchByTextQuery(String query) {
        log.debug("ItemService.patchItem: query {}", query);
        if (Objects.isNull(query) || query.isBlank())
            return new ArrayList<>();

        return itemRepo.searchByTextQuery(query)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private Item getItem(Long id) {
        return itemRepo.findItemById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Item", "id", id)
                );
    }
}
