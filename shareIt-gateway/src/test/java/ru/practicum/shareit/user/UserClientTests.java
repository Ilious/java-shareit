package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserClientTests {

    private static MockRestServiceServer mockServer;

    private static UserClient userClient;

    private static ObjectMapper objectMapper;

    private UserDto user;

    private static Faker faker;

    private static final String ADDRESS = "http://localhost:8080";

    private static final String URL = ADDRESS + "/users";

    @BeforeAll
    static void init() {
        faker = new Faker();
        objectMapper = new ObjectMapper();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory(URL)
        );
        mockServer = MockRestServiceServer.createServer(restTemplate);

        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class, RETURNS_SELF);
        when(builder.build()).thenReturn(restTemplate);

        userClient = new UserClient(ADDRESS, builder);
    }

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name(faker.name().name())
                .email(faker.internet().emailAddress())
                .build();
    }

    @AfterEach
    void tearDown() {
        mockServer.reset();
    }

    @Test
    void getByIdTest() throws JsonProcessingException {
        mockServer.expect(requestTo(URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .body(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getById(1L);
        assertTrue("response is correct", response.getStatusCode().is2xxSuccessful());

        UserDto respObj = objectMapper.convertValue(response.getBody(), UserDto.class);
        assertNotNull("obj is not null", respObj);
        assertEquals("obj is not equal to example", respObj, user);

        mockServer.verify();
    }

    @Test
    void getAllTest() throws JsonProcessingException {
        UserDto anotherUser = UserDto.builder()
                .name(faker.name().name())
                .email(faker.internet().emailAddress())
                .build();

        mockServer.expect(requestTo(URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .body(objectMapper.writeValueAsString(List.of(user, anotherUser)))
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getAll();
        assertTrue("response is correct", response.getStatusCode().is2xxSuccessful());

        List<UserDto> respObj = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        assertNotNull("obj is not null", respObj);
        assertEquals("obj is not equal to example", List.of(user, anotherUser), respObj);

        mockServer.verify();
    }

    @Test
    void postUserTest() throws JsonProcessingException {
        mockServer.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .body(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.postUser(user);
        assertTrue("response is correct", response.getStatusCode().is2xxSuccessful());

        UserDto respObj = objectMapper.convertValue(response.getBody(), UserDto.class);
        assertNotNull("obj is not null", respObj);
        assertEquals("obj is not equal to example", respObj, user);

        mockServer.verify();
    }

    @Test
    void patchUserTest() throws JsonProcessingException {
        mockServer.expect(requestTo(URL + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess()
                        .body(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.patchUser(1, user);
        assertTrue("response is correct", response.getStatusCode().is2xxSuccessful());

        UserDto respObj = objectMapper.convertValue(response.getBody(), UserDto.class);
        assertNotNull("obj is not null", respObj);
        assertEquals("obj is not equal to example", respObj, user);

        mockServer.verify();
    }

    @Test
    void removeByIdTest() {
        mockServer.expect(requestTo(URL + "/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent()
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.removeById(1);
        assertEquals("response is correct", HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        mockServer.verify();
    }
}