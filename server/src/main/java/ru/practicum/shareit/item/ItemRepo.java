package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepo extends CrudRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT it FROM Item AS it " +
            "WHERE it.isAvailable = true " +
            "AND (LOWER(it.description) LIKE LOWER(CONCAT('%', :query, '%'))" +
            "OR  LOWER(it.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Item> searchByTextQuery(String query);

    @Query("SELECT it FROM Item it " +
            "WHERE it.id = :itemId AND it.isAvailable = true")
    Optional<Item> findItemForBooking(Long itemId);

    @Query("SELECT it FROM Item it " +
            "LEFT JOIN FETCH it.comments WHERE it.id = :itemId")
    Optional<Item> findItemWithComments(Long itemId);

    default Item getItemById(Long id) {
        return findItemWithComments(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Item", "id", id)
                );
    }

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(Collection<Long> requestIds);
}
