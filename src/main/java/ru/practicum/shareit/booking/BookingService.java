package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.interfaces.IBookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepo;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepo bookingRepo;

    private final UserRepo userRepo;

    private final ItemRepo itemRepo;

    @Override
    public BookingDto getBookingById(Long id, Long userId) {
        log.debug("BookingService.getBookingById: id {}, userId {}", id, userId);
        userRepo.getUserById(userId);
        Booking booking = bookingRepo.getBookingById(id);

        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId))
            throw new UnauthorizedException(userId);

        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto addBooking(Long userId, BookingDto dto) {
        log.debug("BookingService.addBooking: userId {}, dto {}", userId, dto);
        User userById = userRepo.getUserById(userId);
        Long itemId = dto.itemId();
        itemRepo.getItemById(itemId);
        Item itemById = itemRepo.findItemForBooking(itemId)
                .orElseThrow(InternalServerException::new);
        Booking entity = BookingMapper.toEntity(dto, itemById);
        entity.setItem(itemById);
        entity.setBooker(userById);
        entity.setStatus(BookingStatus.WAITING);

        return BookingMapper.toDto(bookingRepo.save(entity));
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, BookingState state) {
        log.debug("BookingService.getAllBookingByUserId: userId {}, state {}", userId, state);
        userRepo.getUserById(userId);

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepo.findAllByBooker_Id(userId);
            case PAST -> bookingRepo.findAllByBooker_IdAndEndBefore(userId, LocalDate.now());
            case FUTURE -> bookingRepo.findAllByBooker_IdAndStartAfter(userId, LocalDate.now());
            case WAITING -> bookingRepo.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepo.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllBookingByOwnerId(Long userId, BookingState state) {
        log.debug("BookingService.getAllBookingByOwnerId: userId {}, state {}", userId, state);
        userRepo.getUserById(userId);

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepo.findAllByItem_Owner_Id(userId);
            case PAST -> bookingRepo.findAllByItem_Owner_IdAndEndBefore(userId, LocalDate.now());
            case FUTURE -> bookingRepo.findAllByItem_Owner_IdAndStartBefore(userId, LocalDate.now());
            case WAITING -> bookingRepo.findAllByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepo.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Long userId, Long bookingId, Boolean status) {
        log.debug("BookingService.patchBooking: userId {}, bookingId {}", userId, bookingId);
        userRepo.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(userId));
        Booking bookingById = bookingRepo.getBookingById(bookingId);
        if (!Objects.equals(bookingById.getItem().getOwner().getId(), userId))
            throw new UnauthorizedException(userId);

        bookingById.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepo.save(bookingById));
    }
}
