package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfaces.IItemRepo;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemRepo implements IItemRepo {

    private final Map<Long, List<Item>> storage = new HashMap<>();

    @Override
    public Optional<Item> findItemById(Long id) {
        log.trace("ItemRepo.findItemById: id {}", id);

        return storage.values().stream()
                .flatMap(List::stream)
                .filter(it -> it.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        log.trace("ItemRepo.findAll: started");

        return storage.get(userId);
    }

    @Override
    public Item addItem(Long userId, Item item) {
        log.trace("ItemRepo.addItem: {}", item);
        item.setId(getNextIdx());

        storage.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item updItem(Long userId, Item item) {
        log.trace("ItemRepo.updItem: {}", item);
        List<Item> itemsByUserId = storage.get(userId);
        itemsByUserId.stream()
                .filter(it -> it.getId().equals(item.getId()))
                .findFirst()
                .ifPresent(it -> {
                    it.setName(item.getName());
                    it.setDescription(item.getDescription());
                    it.setIsAvailable(item.getIsAvailable());
                });

        return item;
    }

    @Override
    public List<Item> searchByTextQuery(String query) {
        log.trace("ItemRepo.searchByTextQuery: {}", query);

        return storage.values().stream()
                .flatMap(List::stream)
                .filter(it -> it.getIsAvailable() && (it.getName().toLowerCase().contains(query.toLowerCase()) ||
                        it.getDescription().toLowerCase().contains(query.toLowerCase())))
                .toList();
    }

    private Long getNextIdx() {
        return storage.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }
}
