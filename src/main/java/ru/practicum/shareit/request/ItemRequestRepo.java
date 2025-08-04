package ru.practicum.shareit.request;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;

public interface ItemRequestRepo extends CrudRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByOrderByCreatedDesc();

    List<ItemRequest> findAllByIdNotOrderByCreatedDesc(Long id);

    default ItemRequest getItemRequestById(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ItemRequest", "id", id));
    }
}
