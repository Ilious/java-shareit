package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toEntity(BookingDto dto, Item item) {
        return Booking.builder()
                .start(dto.start())
                .end(dto.end())
                .item(item)
                .booker(item.getOwner())
                .status(dto.status())
                .build();
    }

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toDto(
                        booking.getBooker()
                ))
                .item(ItemMapper.toDto(
                        booking.getItem())
                )
                .build();
    }

    public static void patchFields(Booking booking, BookingDto dto) {
        if (Objects.nonNull(dto.start()))
            booking.setStart(dto.start());

        if (Objects.nonNull(dto.end())) {
            booking.setEnd(dto.end());
        }

        if (Objects.nonNull(dto.status()))
            booking.setStatus(dto.status());
    }
}
