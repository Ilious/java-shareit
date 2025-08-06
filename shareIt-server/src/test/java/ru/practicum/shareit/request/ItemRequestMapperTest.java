package ru.practicum.shareit.request;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final Faker faker = new Faker();

    @Test
    void toEntity() {
        User user = User.builder()
                .id(faker.number().randomNumber())
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(faker.number().randomNumber())
                .title(faker.book().title())
                .description(faker.book().author())
                .created(LocalDateTime.now())
                .build();

        ItemRequest entity = ItemRequestMapper.toEntity(dto, user);

        assertNotNull(entity);
        assertAll(() -> {
            assertEquals(dto.title(), entity.getTitle());
            assertEquals(dto.description(), entity.getDescription());
            assertEquals(user.getId(), entity.getRequester().getId());
        });
    }

    @Test
    void toDto() {
        User user = User.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest);

        assertNotNull(dto);
        assertAll(() -> {
            assertEquals(itemRequest.getTitle(), dto.title());
            assertEquals(itemRequest.getDescription(), dto.description());
            assertEquals(itemRequest.getCreated(), dto.created());
            assertEquals(user.getId(), dto.requester().id());
        });
    }

    @Test
    void testToDto() {
        User user = User.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(faker.number().randomNumber())
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(faker.bool().bool())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest, List.of(itemDto));

        assertNotNull(dto);
        assertAll(() -> {
            assertEquals(itemRequest.getTitle(), dto.title());
            assertEquals(itemRequest.getDescription(), dto.description());
            assertEquals(itemRequest.getCreated(), dto.created());
            assertEquals(user.getId(), dto.requester().id());
            assertEquals(1, dto.items().size());
        });
    }
}