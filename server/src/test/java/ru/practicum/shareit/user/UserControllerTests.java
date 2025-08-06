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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private UserDto user;

    private UserDto user2;

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

        user2 = UserDto.builder()
                .name(faker.name().name())
                .email(faker.internet().emailAddress())
                .build();
    }

    @Test
    void getById_shouldReturnUser_Test() throws Exception {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService).getUserById(anyLong());
    }

    @Test
    void getAll_shouldReturnUsers_Test() throws Exception {
        Mockito.when(userService.getAll())
                .thenReturn(List.of(user, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user, user2))));

        verify(userService).getAll();
    }

    @Test
    void createUser_shouldReturnUser_Test() throws Exception {
        Mockito.when(userService.addUser(any()))
                .thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService).addUser(any());
    }

    @Test
    void patchUser_shouldReturnUser_Test() throws Exception {
        Mockito.when(userService.patchUser(anyLong(), any()))
                .thenReturn(user);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService).patchUser(anyLong(), any());
    }

    @Test
    void removeUserById_shouldReturnNoContent_Test() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService).removeUserById(anyLong());
    }
}