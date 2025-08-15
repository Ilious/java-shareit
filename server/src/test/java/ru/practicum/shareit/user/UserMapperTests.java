package ru.practicum.shareit.user;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTests {

    private final Faker faker = new Faker();

    @Test
    void toEntityTest() {
        UserDto user = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        User entity = UserMapper.toEntity(user);

        assertAll(() -> {
            assertEquals(user.name(), entity.getName());
            assertEquals(user.email(), entity.getEmail());
        });
    }

    @Test
    void toDtoTest() {
        User user = User.builder()
                .id(1L)
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        UserDto entity = UserMapper.toDto(user);

        assertNotNull(entity);
        assertAll(() -> {
            assertEquals(user.getId(), entity.id());
            assertEquals(user.getName(), entity.name());
            assertEquals(user.getEmail(), entity.email());
        });
    }

    @Test
    void patchFieldsTest() {
        User user = User.builder()
                .id(1L)
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();

        UserDto dto = UserDto.builder()
                .name(faker.name().username())
                .build();
        UserMapper.patchFields(user, dto);

        assertNotNull(user);
        assertAll(() -> {
            assertNotEquals(user.getId(), dto.id());
            assertEquals(user.getName(), dto.name());
            assertNotEquals(user.getEmail(), dto.email());
        });
    }
}