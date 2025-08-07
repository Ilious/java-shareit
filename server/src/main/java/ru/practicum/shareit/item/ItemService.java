package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.IItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepo;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    private final UserRepo userRepo;

    private final ItemRepo itemRepo;

    private final ItemRequestRepo itemRequestRepo;

    private final CommentRepo commentRepo;

    private final BookingRepo bookingRepo;

    @Override
    public ItemDto getItemById(Long id) {
        log.debug("ItemService.getItemById: id {}", id);
        Item itemById = itemRepo.getItemById(id);
        return ItemMapper.toDto(itemById);
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        log.debug("ItemService.getAll: started");
        userRepo.getUserById(userId);
        List<Item> items = itemRepo.findAllByOwnerId(userId);
        if (items.isEmpty())
            throw new EntityNotFoundException("Items", "storage", "any");

        return items.stream()
                .map(it -> {
                    LocalDateTime now = LocalDateTime.now();
                    Booking first = bookingRepo.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(it.getId(), now);
                    Booking last = bookingRepo.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(it.getId(), now);
                    return ItemMapper.toDto(it, first, last);
                })
                .toList();
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto dto) {
        log.debug("ItemService.addItem: {}", dto);

        User userById = userRepo.getUserById(userId);
        ItemRequest requestById = Objects.isNull(dto.requestId()) ?
                null :
                itemRequestRepo.getItemRequestById(dto.requestId());

        Item entity = ItemMapper.toEntity(dto, userById, requestById);

        return ItemMapper.toDto(itemRepo.save(entity));
    }

    @Override
    @Transactional
    public ItemDto patchItem(Long id, ItemDto dto, Long userId) {
        log.debug("ItemService.patchItem: id {}, dto {}", id, dto);
        userRepo.getUserById(userId);
        Item item = itemRepo.getItemById(id);
        ItemMapper.patchFields(item, dto);

        return ItemMapper.toDto(itemRepo.save(item));
    }

    @Override
    public List<ItemDto> searchByTextQuery(String query) {
        log.debug("ItemService.patchItem: query {}", query);

        return itemRepo.searchByTextQuery(query)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User userById = userRepo.getUserById(userId);
        Item itemById = itemRepo.getItemById(itemId);

        if (!bookingRepo.existsBookingByBooker_IdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now()))
            throw new BadRequestException("booking not exists by bookerId %d and itemId %d".formatted(userId, itemId));

        Comment entity = CommentMapper.toEntity(dto, userById, itemById);

        return CommentMapper.toDto(commentRepo.save(entity));
    }
}
