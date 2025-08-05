package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTests {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private ItemRequestDto itemRequest;

    @BeforeAll
    static void init() {
        faker = new Faker();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.internet().uuid())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getRequestById_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestClient.getItemByID(1L))
                .thenReturn(ResponseEntity.ok().body(itemRequest));

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequest)));

        verify(itemRequestClient).getItemByID(1L);
    }

    @Test
    void getRequestById_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/requests/-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    void getRequests_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestClient.getRequests(1L))
                .thenReturn(ResponseEntity.ok().body(itemRequest));

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequest)));

        verify(itemRequestClient).getRequests(anyLong());
    }

    @Test
    void getRequestsAll_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestClient.getAll(anyLong()))
                .thenReturn(ResponseEntity.ok().body(itemRequest));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequest)));

        verify(itemRequestClient).getAll(anyLong());
    }

    @Test
    void getRequestsAll_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, -1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    void postRequest_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestClient.postItemReq(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemRequest));

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequest)));

        verify(itemRequestClient).postItemReq(anyLong(), any());
    }

    @Test
    void postRequest_titleIsNullOrBlank_shouldReturn400_Test() throws Exception {
        ItemRequestDto badItem = ItemRequestDto.builder().title(null)
                .created(LocalDateTime.now())
                .description(faker.name().name())
                .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        badItem = ItemRequestDto.builder().title("      ")
                .created(LocalDateTime.now())
                .description(faker.name().name())
                .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    void postRequest_dateIsNullOrInvalid_shouldReturn400_Test() throws Exception {
        ItemRequestDto badItem = ItemRequestDto.builder().title(faker.book().title())
                .created(null)
                .description(faker.name().name())
                .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        badItem = ItemRequestDto.builder().title("      ")
                .created(LocalDateTime.now().plusSeconds(10))
                .description(faker.name().name())
                .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }
}