package ru.practicum.shareit.booking;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Profile("test")
@Transactional
class BookingServiceTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private final Faker faker = new Faker();

    private BookingDto booking;

    private UserDto user;

    private UserDto anotherUser;

    private ItemDto item;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        user = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        user = userService.addUser(user);

        UserDto builtUser = UserDto.builder()
                .name(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
        anotherUser = userService.addUser(builtUser);

        item = ItemDto.builder()
                .name(faker.book().title())
                .description(faker.book().author())
                .isAvailable(true)
                .build();
        item = itemService.addItem(user.id(), item);

        booking = BookingDto.builder()
                .start(now)
                .end(now.plusDays(1))
                .itemId(item.id())
                .build();
    }

    @Test
    void getBookingByIdTest() {
        BookingDto savedBooking = bookingService.addBooking(user.id(), booking);

        assertDoesNotThrow(() -> bookingService.getBookingById(savedBooking.id(), user.id()));
        BookingDto bookingById = bookingService.getBookingById(savedBooking.id(), user.id());

        assertNotNull(bookingById);
        assertAll(() -> {
            assertEquals(savedBooking.start(), bookingById.start());
            assertEquals(savedBooking.end(), bookingById.end());
            assertEquals(BookingStatus.WAITING, bookingById.status());
        });
    }

    @Test
    void addBookingTest() {
        BookingDto savedBooking = bookingService.addBooking(user.id(), booking);

        assertNotNull(savedBooking);
        assertNotNull(savedBooking.id());
        assertAll(() -> {
            assertEquals(booking.start(), savedBooking.start());
            assertEquals(booking.end(), savedBooking.end());
            assertEquals(BookingStatus.WAITING, savedBooking.status());
            assertEquals(user.id(), savedBooking.booker().id());
            assertEquals(item.id(), savedBooking.item().id());
        });
    }

    @Test
    void getAllBookingByUserIdTest() {
        BookingDto savedBooking = bookingService.addBooking(user.id(), booking);

        BookingDto savedBooking2 = bookingService.addBooking(anotherUser.id(), booking);
        for (BookingState status : BookingState.values()) {
            List<BookingDto> bookingsByUserId = bookingService.getAllBookingByUserId(user.id(), status);

            if (status.equals(BookingState.WAITING) || status.equals(BookingState.ALL)) {
                assertNotNull(bookingsByUserId);
                assertEquals(1, bookingsByUserId.size());
                assertTrue(bookingsByUserId.contains(savedBooking));
            }  else {
                assertNotNull(bookingsByUserId);
                assertTrue(bookingsByUserId.isEmpty());
            }
        }
    }

    @Test
    void getAllBookingByOwnerIdTest() {
        BookingDto savedBooking = bookingService.addBooking(user.id(), booking);
        ItemDto anotherItem = itemService.addItem(anotherUser.id(), item.toBuilder()
                .id(null)
                .name(faker.book().title())
                .build());

        BookingDto savedBooking2 = bookingService.addBooking(anotherUser.id(), booking.toBuilder()
                .id(null)
                .itemId(anotherItem.id())
                .build());

        List<BookingDto> bookingsByUserId = bookingService.getAllBookingByOwnerId(anotherUser.id(), BookingState.WAITING);

        assertNotNull(bookingsByUserId);
        assertEquals(1, bookingsByUserId.size());
        assertEquals(bookingsByUserId.getFirst().id(), (savedBooking2.id()));
    }

    @Test
    void patchBookingTest() {
        booking = bookingService.addBooking(user.id(), booking);
        BookingDto updBooking = bookingService.patchBooking(user.id(), booking.id(), true);

        assertNotNull(updBooking);
        assertEquals(BookingStatus.APPROVED, updBooking.status());
    }
}