package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingClientTests {

    private static ObjectMapper objectMapper;

    private static BookingClient bookingClient;

    private static final String ADDRESS = "http://localhost:8080";

    private static final String URL = ADDRESS + "/bookings";

    private static MockRestServiceServer server;

    private BookingDto booking;

    private BookingDto booking2;

    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory(URL)
        );
        server = MockRestServiceServer.createServer(restTemplate);

        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class, Mockito.RETURNS_SELF);
        Mockito.when(builder.build()).thenReturn(restTemplate);

        bookingClient = new BookingClient(ADDRESS, builder);
    }

    @BeforeEach
    void setUp() {
        booking = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now().plusDays(2))
                .build();

        booking2 = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now().plusDays(2))
                .build();
    }

    @AfterEach
    void tearDown() {
        server.reset();
    }

    @Test
    void getByIdTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(booking)));

        ResponseEntity<Object> responseObject = bookingClient.getById(2, 1L);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        BookingDto object = objectMapper.convertValue(responseObject.getBody(), BookingDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", booking, object);

        server.verify();
    }

    @Test
    void getByUserTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(booking)));

        ResponseEntity<Object> responseObject = bookingClient.getByUser(2, BookingState.ALL);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        BookingDto object = objectMapper.convertValue(responseObject.getBody(), BookingDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", booking, object);

        server.verify();
    }

    @Test
    void getByOwnerTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/owner?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(List.of(booking, booking2))));

        ResponseEntity<Object> responseObject = bookingClient.getByOwner(2, BookingState.ALL);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        List<BookingDto> object = objectMapper.convertValue(responseObject.getBody(), new TypeReference<>() {
        });

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", List.of(booking, booking2), object);

        server.verify();
    }

    @Test
    void postBookingTest() throws JsonProcessingException {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(booking)));

        ResponseEntity<Object> responseObject = bookingClient.postBooking(2, booking);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        BookingDto object = objectMapper.convertValue(responseObject.getBody(), BookingDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", booking, object);

        server.verify();
    }

    @Test
    void patchBookingTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/1?approved=false"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(booking)));

        ResponseEntity<Object> responseObject = bookingClient.patchBooking(2, 1L, false);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        BookingDto object = objectMapper.convertValue(responseObject.getBody(), BookingDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", booking, object);

        server.verify();
    }
}