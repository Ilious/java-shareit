package ru.practicum.shareit.booking;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTests {

    private final Faker faker = new Faker();

    @Test
    void toEntity() {
        User user = User.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        Item item = Item.builder()
                .name(faker.name().username())
                .description(faker.internet().emailAddress())
                .isAvailable(faker.bool().bool())
                .build();


        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Booking entity = BookingMapper.toEntity(dto, item, user);

        assertNotNull(entity);
        assertAll(() -> {
            assertEquals(dto.start(), entity.getStart());
            assertEquals(dto.end(), entity.getEnd());
            assertEquals(BookingStatus.WAITING, entity.getStatus());
        });
    }

    @Test
    void toDto() {
        Booking entity = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto dto = BookingMapper.toDto(entity);

        assertNotNull(dto);
        assertAll(() -> {
            assertEquals(entity.getStart(), dto.start());
            assertEquals(entity.getEnd(), dto.end());
            assertEquals(entity.getStatus(), dto.status());
        });
    }
}