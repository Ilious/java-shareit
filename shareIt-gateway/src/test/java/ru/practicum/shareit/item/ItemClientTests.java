package ru.practicum.shareit.item;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemClientTests {

    private final static String HEADER_USER_ID =  "X-Sharer-User-Id";

    private static MockRestServiceServer server;

    private ItemDto item;

    private ItemDto item2;

    private CommentDto comment;

    private static ItemClient itemClient;

    private final static String ADDRESS = "http://localhost:8080";

    private final static String URL = ADDRESS + "/items";

    private static Faker faker;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        faker = new Faker();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory(URL)
        );
        server = MockRestServiceServer.createServer(restTemplate);

        RestTemplateBuilder builderMock = Mockito.mock(RestTemplateBuilder.class, Mockito.RETURNS_SELF);
        Mockito.when(builderMock.build()).thenReturn(restTemplate);
        itemClient = new ItemClient(URL, builderMock);
    }

    @BeforeEach
    void setUp() {
        item = ItemDto.builder()
                .name(faker.book().title())
                .description(faker.book().author())
                .isAvailable(false)
                .build();

        item2 = ItemDto.builder()
                .name(faker.book().title())
                .description(faker.book().author())
                .isAvailable(false)
                .build();

        comment = CommentDto.builder()
                .created(LocalDateTime.now())
                .authorName(faker.name().username())
                .text(faker.name().fullName())
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
                                .body(objectMapper.writeValueAsString(item)));

        ResponseEntity<Object> responseObject = itemClient.getById(1);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        ItemDto object = objectMapper.convertValue(responseObject.getBody(), ItemDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", item, object);

        server.verify();
    }

    @Test
    void getByAllUserIdTest() throws JsonProcessingException {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HEADER_USER_ID, String.valueOf(1)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(List.of(item, item2))));

        ResponseEntity<Object> responseObject = itemClient.getByAllUserId(1);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        List<ItemDto> object = objectMapper.convertValue(responseObject.getBody(), new TypeReference<>() {
        });

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", List.of(item, item2), object);

        server.verify();
    }

    @Test
    void postItemTest() throws JsonProcessingException {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_USER_ID, String.valueOf(1)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(item)));

        ResponseEntity<Object> responseObject = itemClient.postItem(1, item);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        ItemDto object = objectMapper.convertValue(responseObject.getBody(), ItemDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", item, object);

        server.verify();
    }

    @Test
    void patchItemTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/2"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HEADER_USER_ID, String.valueOf(1)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(item)));

        ResponseEntity<Object> responseObject = itemClient.patchItem(2, 1, item);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        ItemDto object = objectMapper.convertValue(responseObject.getBody(), ItemDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", item, object);

        server.verify();
    }

    @Test
    void searchByTextTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/search?text=query"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(List.of(item, item2))));

        ResponseEntity<Object> responseObject = itemClient.searchByText("query");
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        List<ItemDto> object = objectMapper.convertValue(responseObject.getBody(), new TypeReference<>() {
        });

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", List.of(item, item2), object);

        server.verify();
    }

    @Test
    void postCommentTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/2/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_USER_ID, String.valueOf(1)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(comment)));

        ResponseEntity<Object> responseObject = itemClient.postComment(1, 2L, comment);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        CommentDto object = objectMapper.convertValue(responseObject.getBody(), CommentDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", comment, object);

        server.verify();
    }
}