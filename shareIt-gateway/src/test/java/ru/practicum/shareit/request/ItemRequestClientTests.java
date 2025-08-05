package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemRequestClientTests {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private static final String ADDRESS = "http://localhost:8080";

    private static final String URL = ADDRESS + "/requests";

    private static ObjectMapper mapper;

    private static ItemRequestClient itemRequestClient;

    private static MockRestServiceServer server;

    private static Faker faker;

    private ItemRequestDto itemRequest;

    private ItemRequestDto itemRequest2;

    @BeforeAll
    static void init() {
        faker = new Faker();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory(URL)
        );
        server = MockRestServiceServer.createServer(restTemplate);

        RestTemplateBuilder builderMock = Mockito.mock(RestTemplateBuilder.class, Mockito.RETURNS_SELF);
        Mockito.when(builderMock.build()).thenReturn(restTemplate);
        itemRequestClient = new ItemRequestClient(URL, builderMock);
    }

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .created(LocalDateTime.now())
                .build();

        itemRequest2 = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.book().author())
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        server.reset();
    }

    @Test
    void getItemByIDTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(itemRequest)));

        ResponseEntity<Object> responseObject = itemRequestClient.getItemByID(1L);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        ItemRequestDto object = mapper.convertValue(responseObject.getBody(), ItemRequestDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", itemRequest, object);

        server.verify();
    }

    @Test
    void getRequestsTest() throws JsonProcessingException {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(List.of(itemRequest, itemRequest2))));

        ResponseEntity<Object> responseObject = itemRequestClient.getRequests(1L);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        List<ItemRequestDto> object = mapper.convertValue(responseObject.getBody(), new TypeReference<>() {
        });

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", List.of(itemRequest, itemRequest2), object);

        server.verify();
    }

    @Test
    void getAllTest() throws JsonProcessingException {
        server.expect(requestTo(URL + "/all"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, String.valueOf(1))
                        .body(mapper.writeValueAsString(List.of(itemRequest, itemRequest2))));

        ResponseEntity<Object> responseObject = itemRequestClient.getAll(1L);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        List<ItemRequestDto> object = mapper.convertValue(responseObject.getBody(), new TypeReference<>() {
        });

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", List.of(itemRequest, itemRequest2), object);

        server.verify();
    }

    @Test
    void postItemReqTest() throws JsonProcessingException {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(itemRequest)));

        ResponseEntity<Object> responseObject = itemRequestClient.postItemReq(1L, itemRequest);
        assertTrue("response is correct", responseObject.getStatusCode().is2xxSuccessful());

        ItemRequestDto object = mapper.convertValue(responseObject.getBody(), ItemRequestDto.class);

        assertNotNull("obj is not null", object);
        assertEquals("obj is not equal to example", itemRequest, object);

        server.verify();
    }
}