package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTests {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private BookingDto booking;


    @BeforeAll
    static void init() {
        faker = new Faker();
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
    }

    @Test
    void getById_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingClient.getById(1, 2L))
                .thenReturn(ResponseEntity.ok().body(booking));

        mockMvc.perform(get("/bookings/2")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingClient).getById(1, 2L);
    }

    @Test
    void getById_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/bookings/2")
                        .header(HEADER_USER_ID, "-1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/-2")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void getAllBookingsByUser_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingClient.getByUser(1, BookingState.ALL))
                .thenReturn(ResponseEntity.ok().body(booking));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingClient).getByUser(1, BookingState.ALL);
    }

    @Test
    void getAllBookingsByUser_shouldReturn400or500_Test() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL")
                        .header(HEADER_USER_ID, "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void getAllBookingsByUserItems() throws Exception {
        Mockito.when(bookingClient.getByOwner(1, BookingState.ALL))
                .thenReturn(ResponseEntity.ok().body(booking));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingClient).getByOwner(1, BookingState.ALL);
    }

    @Test
    void getAllBookingsByOwner_shouldReturn400or500_Test() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header(HEADER_USER_ID, "-1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/owner?state=ALL1")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingClient.postBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(booking));

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingClient).postBooking(anyLong(), any());
    }

    @Test
    void createBooking_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_DateIsNullOrInvalid_shouldReturn400_Test() throws Exception {
        BookingDto badBooking = BookingDto.builder()
                .start(null)
                .end(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badBooking)))
                .andExpect(status().isBadRequest());

        badBooking = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badBooking)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void patchBooking_shouldReturnBooking_Test() throws Exception {
        Mockito.when(bookingClient.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(booking));

        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));

        verify(bookingClient).patchBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void patchBooking_shouldReturn400or500_Test() throws Exception {
        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header(HEADER_USER_ID, "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/bookings/-1?approved=false")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}