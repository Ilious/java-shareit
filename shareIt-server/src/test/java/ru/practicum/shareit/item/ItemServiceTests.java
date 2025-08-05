package ru.practicum.shareit.item;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("test")
@Transactional
class ItemServiceTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;
    
    private final Faker faker = new Faker();
    
    private ItemDto item;

    private ItemDto anotherItem;

    private UserDto user;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        user = userService.addUser(user);

        item = ItemDto.builder()
                .name(faker.book().title())
                .description(faker.lorem().sentence())
                .isAvailable(true)
                .build();

        anotherItem = ItemDto.builder()
                .name(faker.book().title())
                .description(faker.lorem().sentence())
                .isAvailable(false)
                .build();
    }

    @Test
    void getItemById() {
        ItemDto savedItem = itemService.addItem(user.id(), item);

        assertDoesNotThrow(() -> itemService.getItemById(savedItem.id()));
        ItemDto itemById = itemService.getItemById(savedItem.id());

        assertNotNull(itemById);
        assertAll(() -> {
            assertEquals(savedItem.name(), itemById.name());
            assertEquals(savedItem.description(), itemById.description());
            assertEquals(savedItem.isAvailable() , itemById.isAvailable());
        });
    }

    @Test
    void getAllByUserId() {
        ItemDto savedItem = itemService.addItem(user.id(), item);

        ItemDto savedItem2 = itemService.addItem(user.id(), anotherItem);

        assertDoesNotThrow(() -> itemService.getAllByUserId(user.id()));
        List<ItemDto> bookingsByUserId = itemService.getAllByUserId(user.id());

        assertEquals(2, bookingsByUserId.size());
    }

    @Test
    void addItem() {
        ItemDto savedItem = itemService.addItem(user.id(), item);

        assertNotNull(savedItem);
        assertNotNull(savedItem.id());
        assertAll(() -> {
            assertEquals(savedItem.name(), item.name());
            assertEquals(savedItem.description(), item.description());
            assertEquals(savedItem.isAvailable() , item.isAvailable());
        });
    }

    @Test
    void patchItem() {
        ItemDto savedItem = itemService.addItem(user.id(), item);

        ItemDto updItem = itemService.patchItem(savedItem.id(), anotherItem, user.id());

        assertAll(() -> {
            assertEquals(anotherItem.name(), updItem.name());
            assertEquals(anotherItem.description(), updItem.description());
            assertEquals(anotherItem.isAvailable() , updItem.isAvailable());
        });
    }

    @Test
    void searchByTextQuery() {
        String query = faker.lorem().word();
        ItemDto firstItem = ItemDto.builder()
                .name(" " + query + " ")
                .description(faker.lorem().sentence())
                .isAvailable(true)
                .build();
        ItemDto savedItem = itemService.addItem(user.id(), firstItem);

        ItemDto secondItem = ItemDto.builder()
                .name(faker.book().title())
                .description(faker.lorem().word() + query + faker.lorem().word())
                .isAvailable(true)
                .build();
        ItemDto savedItem2 = itemService.addItem(user.id(), secondItem);
        List<ItemDto> bookingsByUserId = itemService.searchByTextQuery(query);

        assertNotNull(bookingsByUserId);
        assertEquals(2, bookingsByUserId.size());
    }

    @Test
    void addComment() {
        UserDto anotherUser = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        anotherUser = userService.addUser(anotherUser);

        item = itemService.addItem(user.id(), item);

        BookingDto booking = BookingDto.builder()
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now())
                .itemId(item.id())
                .build();
        bookingService.addBooking(anotherUser.id(), booking);

        CommentDto comment = CommentDto.builder()
                .authorName(faker.name().username())
                .text(faker.lorem().sentence())
                .build();

        UserDto finalAnotherUser = anotherUser;
        assertDoesNotThrow(() -> itemService.addComment(finalAnotherUser.id(), item.id(), comment));

        em.flush();
        em.clear();

        assertFalse(itemService.getItemById(item.id()).comments().isEmpty());
    }
}