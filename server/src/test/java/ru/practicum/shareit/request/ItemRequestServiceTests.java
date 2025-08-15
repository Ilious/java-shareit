package ru.practicum.shareit.request;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("test")
@Transactional
class ItemRequestServiceTests {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private final Faker faker = new Faker();

    private ItemRequestDto requestDto;

    private UserDto user;

    @BeforeEach
    void setUp() {
        requestDto = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .build();

        user = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        user = userService.addUser(user);
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto savedReq = itemRequestService.addItemRequest(user.id(), requestDto);

        assertDoesNotThrow(() -> itemRequestService.getItemRequestById(savedReq.id()));
        ItemRequestDto requestById = itemRequestService.getItemRequestById(savedReq.id());

        assertAll(() -> {
            assertEquals(requestById.title(), requestDto.title());
            assertEquals(requestById.description(), requestDto.description());
            assertTrue(requestById.created().isBefore(LocalDateTime.now()));
            assertEquals(requestById.requester().id(), user.id());
        });
    }

    @Test
    void getRequestsAll() {
        requestDto = itemRequestService.addItemRequest(user.id(), requestDto);

        UserDto anotherUser = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        anotherUser = userService.addUser(anotherUser);

        ItemRequestDto request = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .build();
        request = itemRequestService.addItemRequest(anotherUser.id(), request);

        List<ItemRequestDto> requestsAll = itemRequestService.getRequestsAll(user.id());

        assertNotNull(requestsAll);
        ItemRequestDto finalRequest = request;
        assertAll(() -> {
            assertFalse(requestsAll.isEmpty());
            assertEquals(1, requestsAll.size());
            assertEquals(finalRequest.id(), requestsAll.getFirst().id());
        });
    }

    @Test
    void getRequestExceptUser() {
        requestDto = itemRequestService.addItemRequest(user.id(), requestDto);

        UserDto anotherUser = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        anotherUser = userService.addUser(anotherUser);

        ItemRequestDto request = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .build();
        request = itemRequestService.addItemRequest(anotherUser.id(), request);

        List<ItemRequestDto> reequests = itemRequestService.getRequestsAll(user.id());

        assertNotNull(reequests);
        ItemRequestDto finalRequest = request;
        assertAll(() -> {
            assertFalse(reequests.isEmpty());
            assertEquals(1, reequests.size());
            assertEquals(finalRequest.id(), reequests.getFirst().id());
        });
    }

    @Test
    void addItemRequest() {
        ItemRequestDto savedReq = itemRequestService.addItemRequest(user.id(), requestDto);

        assertNotNull(savedReq);
        assertNotNull(savedReq.id());
        assertAll(() -> {
            assertEquals(savedReq.title(), requestDto.title());
            assertEquals(savedReq.description(), requestDto.description());
            assertTrue(savedReq.created().isBefore(LocalDateTime.now()));
            assertEquals(savedReq.requester().id(), user.id());
        });
    }
}