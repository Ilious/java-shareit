package ru.practicum.shareit.item;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMappersTest {

    private final Faker faker = new Faker();

    @Test
    void toEntityTest() {
        User user = User.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence())
                .created(LocalDateTime.now())
                .build();

        ItemDto dto = ItemDto.builder()
                .id(faker.number().randomNumber())
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(faker.bool().bool())
                .build();

        Item entity = ItemMapper.toEntity(dto, user, itemRequest);

        assertNotNull(entity);
        assertAll(() -> {
            assertEquals(dto.name(), entity.getName());
            assertEquals(dto.description(), entity.getDescription());
        });
    }

    @Test
    void toDtoTest() {
        Item entity = Item.builder()
                .id(faker.number().randomNumber())
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(faker.bool().bool())
                .build();

        ItemDto dto = ItemMapper.toDto(entity);

        assertNotNull(entity);
        assertAll(() -> {
            assertEquals(entity.getId(), dto.id());
            assertEquals(entity.getName(), dto.name());
            assertEquals(entity.getDescription(), dto.description());
            assertEquals(entity.getIsAvailable(), dto.isAvailable());
        });
    }

    @Test
    void toDtoWithBookingsTest() {
        Booking booking = Booking.builder()
                .id(faker.number().randomNumber())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        Item entity = Item.builder()
                .id(faker.number().randomNumber())
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(faker.bool().bool())
                .build();

        ItemDto dto = ItemMapper.toDto(entity, booking, booking);

        assertNotNull(dto);
        assertAll(() -> {
            assertEquals(dto.id(), entity.getId());
            assertEquals(dto.name(), entity.getName());
            assertEquals(dto.description(), entity.getDescription());
            assertEquals(dto.isAvailable(), entity.getIsAvailable());
            assertEquals(dto.lastBooking().id(), booking.getId());
            assertEquals(dto.nextBooking().id(), booking.getId());
        });
    }

    @Test
    void patchFieldsTest() {
        Item entity = Item.builder()
                .id(faker.number().randomNumber())
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(faker.bool().bool())
                .build();

        ItemDto dto = ItemDto.builder()
                .name(faker.commerce().productName())
                .isAvailable(faker.bool().bool())
                .build();

        ItemMapper.patchFields(entity, dto);
        assertAll(() -> {
            assertEquals(dto.name(), entity.getName());
            assertEquals(dto.isAvailable(), entity.getIsAvailable());
            assertNotEquals(dto.description(), entity.getDescription());
        });
    }
}