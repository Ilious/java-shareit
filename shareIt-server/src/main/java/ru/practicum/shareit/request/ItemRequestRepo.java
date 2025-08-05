package ru.practicum.shareit.request;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepo extends CrudRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);;

    default ItemRequest getItemRequestById(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ItemRequest", "id", id));
    }

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId);
}
