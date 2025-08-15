package ru.practicum.shareit.user;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("test")
@Transactional
class UserServiceTests {

    @Autowired
    private UserService userService;

    private static final Faker faker = new Faker();

    private UserDto user;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
    }

    @Test
    void getUserByIdTest() {
        UserDto savedUser = userService.addUser(user);

        assertDoesNotThrow(() -> userService.getUserById(savedUser.id()));
        UserDto userById = userService.getUserById(savedUser.id());

        assertEquals(savedUser.name(), userById.name());
        assertEquals(savedUser.email(), userById.email());
    }

    @Test
    void getAllTest() {
        UserDto savedUser = userService.addUser(user);
        UserDto anotherUser = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        UserDto anotherSaved = userService.addUser(anotherUser);

        List<UserDto> users = userService.getAll();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void addUserTest() {
        UserDto savedUser = userService.addUser(user);

        assertNotNull(savedUser);
        assertAll(() -> {
            assertEquals(user.name(), savedUser.name());
            assertEquals(user.email(), savedUser.email());
        });
    }

    @Test
    void patchUserTest() {
        UserDto savedUser = userService.addUser(user);
        UserDto anotherUser = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        userService.patchUser(savedUser.id(), anotherUser);

        UserDto userById = userService.getUserById(savedUser.id());

        assertAll(() -> {
            assertEquals(userById.name(), anotherUser.name());
            assertEquals(userById.email(), anotherUser.email());
        });
    }

    @Test
    void removeUserByIdTest() {
        UserDto savedUser = userService.addUser(user);
        userService.removeUserById(savedUser.id());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(savedUser.id()));
    }
}