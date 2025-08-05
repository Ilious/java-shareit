package ru.practicum.shareit.booking;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepo extends CrudRepository<Booking, Long> {

    List<Booking> findAllByBooker_Id(Long bookerId);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndStartAfter(Long bookerId, LocalDateTime startAfter);

    List<Booking> findAllByBooker_IdAndEndBefore(Long booker_id, LocalDateTime end);

    List<Booking> findAllByItem_Owner_Id(Long itemOwnerId);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long itemOwnerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStartBefore(Long itemOwnerId, LocalDateTime startBefore);

    List<Booking> findAllByItem_Owner_IdAndEndBefore(Long itemOwnerId, LocalDateTime endBefore);

    boolean existsBookingByBooker_IdAndItem_IdAndEndBefore(Long bookerId, Long itemId, LocalDateTime endBefore);

    Booking findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime startAfter);

    Booking findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime endBefore);

    default Booking getBookingById(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking", "id", id));
    }
}
