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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
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
    private ItemRequestService itemRequestService;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private ItemRequestDto itemRequest;

    private ItemRequestDto itemRequest2;

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

        itemRequest2 = ItemRequestDto.builder()
                .title(faker.book().title())
                .description(faker.internet().uuid())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getRequestById_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestService.getItemRequestById(anyLong()))
                .thenReturn(itemRequest);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequest)));

        verify(itemRequestService).getItemRequestById(anyLong());
    }

    @Test
    void getRequests_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestService.getRequestExceptUser(anyLong()))
                .thenReturn(List.of(itemRequest, itemRequest2));

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequest, itemRequest2))));

        verify(itemRequestService).getRequestExceptUser(anyLong());
    }

    @Test
    void getRequestsAll_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestService.getRequestsAll(anyLong()))
                .thenReturn(List.of(itemRequest, itemRequest2));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequest, itemRequest2))));

        verify(itemRequestService).getRequestsAll(anyLong());
    }

    @Test
    void postRequest_shouldReturnRequest_Test() throws Exception {
        Mockito.when(itemRequestService.addItemRequest(anyLong(), any()))
                .thenReturn(itemRequest);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequest)));

        verify(itemRequestService).addItemRequest(anyLong(), any());
    }
}