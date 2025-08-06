package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.interfaces.IBookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTests {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingService bookingService;

    private static ObjectMapper objectMapper;

    private BookingDto booking;

    private BookingDto booking2;

    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    void setUp() {
        booking = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        booking2 = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void getById_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/2")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingService).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getAllBookingsByUser_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingService.getAllBookingByUserId(anyLong(), any()))
                .thenReturn(List.of(booking, booking2));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking, booking2))));

        verify(bookingService).getAllBookingByUserId(anyLong(), any());
    }

    @Test
    void getAllBookingsByUserItems() throws Exception {
        Mockito.when(bookingService.getAllBookingByOwnerId(anyLong(), any()))
                .thenReturn(List.of(booking, booking2));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking, booking2))));

        verify(bookingService).getAllBookingByOwnerId(anyLong(), any());
    }

    @Test
    void createBooking_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingService).addBooking(anyLong(), any());
    }

    @Test
    void patchBooking_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingService.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingService).patchBooking(anyLong(), anyLong(), anyBoolean());
    }
}