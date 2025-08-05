package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private UserDto user;

    @BeforeAll
    static void init() {
        faker = new Faker();
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name(faker.name().name())
                .email(faker.internet().emailAddress())
                .build();
    }

    @Test
    void getById_shouldReturnUser_Test() throws Exception {
        Mockito.when(userClient.getById(1))
                .thenReturn(ResponseEntity.ok().body(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userClient).getById(1);
    }

    @Test
    void getById_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/users/-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void getAll_shouldReturnUsers_Test() throws Exception {
        Mockito.when(userClient.getAll())
                .thenReturn(ResponseEntity.ok().body(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userClient).getAll();
    }

    @Test
    void createUser_shouldReturnUser_Test() throws Exception {
        Mockito.when(userClient.postUser(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(user));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userClient).postUser(any());
    }

    @Test
    void createUser_nameIsNullOrBlank_shouldReturn400_Test() throws Exception {
        UserDto badUser = UserDto.builder().name(null).email(faker.internet().emailAddress()).build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        badUser = UserDto.builder().name("      ").email(faker.internet().emailAddress()).build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void createUser_emailIsNullOrBlank_shouldReturn400_Test() throws Exception {
        UserDto badUser = UserDto.builder().name(faker.name().username()).email(null).build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        badUser = UserDto.builder().name(faker.name().username()).email("      ").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void createUser_emailIsInvalid_shouldReturn400_Test() throws Exception {
        UserDto badUser = UserDto.builder().name(faker.name().username()).email("email.ru").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void patchUser_shouldReturnUser_Test() throws Exception {
        Mockito.when(userClient.patchUser(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(user));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userClient).patchUser(anyLong(), any());
    }

    @Test
    void patchUer_emailIsBlankOrInvalid_shouldReturn400_Test() throws Exception {
        UserDto badUser = UserDto.builder().email("      ").build();
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        badUser = UserDto.builder().email("bad.mail").build();
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUser)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void patchUser_shouldReturn400_Test() throws Exception {
        mockMvc.perform(patch("/users/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void removeUserById_shouldReturnNoContent_Test() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userClient).removeById(1);
    }

    @Test
    void removeUserById_shouldReturn400_Test() throws Exception {
        mockMvc.perform(delete("/users/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }
}