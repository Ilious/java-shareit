package ru.practicum.shareit.item.interfaces;


import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface IItemRepo {

    Optional<Item> findItemById(Long id);

    List<Item> findAllByUserId(Long userId);

    Item addItem(Long userId, Item dto);

    Item updItem(Long userId, Item dto);

    List<Item> searchByTextQuery(String query);
}
