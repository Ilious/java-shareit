package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTests {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private ItemDto item;

    private CommentDto comment;

    @BeforeAll
    static void init() {
        faker = new Faker();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    void setUp() {
        item = ItemDto.builder()
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(faker.bool().bool())
                .build();

        comment = CommentDto.builder()
                .text(faker.lorem().sentence())
                .authorName(faker.name().fullName())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItemById_shouldReturnItem_Test() throws Exception {
        when(itemClient.getById(1L))
                .thenReturn(ResponseEntity.ok().body(item));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemClient).getById(1L);
    }

    @Test
    void getItemById_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/items/-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void getAllByUserId_shouldReturnItems_Test() throws Exception {
        when(itemClient.getByAllUserId(1L))
                .thenReturn(ResponseEntity.ok().body(item));

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemClient).getByAllUserId(1L);
    }

    @Test
    void getAllByUserId_shouldReturn400_Test() throws Exception {
        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void postItem_shouldReturnItem_Test() throws Exception {
        when(itemClient.postItem(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(item));

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemClient).postItem(anyLong(), any());
    }

    @Test
    void postItem_invalidUserId_shouldReturn400_Test() throws Exception {
        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void postItem_nameNullOrBlank_shouldReturn400_Test() throws Exception {
        ItemDto badItem = ItemDto.builder().name(null)
                .description(faker.lorem().sentence())
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        badItem = ItemDto.builder().name("   ")
                .description(faker.lorem().sentence())
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void postItem_availableNull_shouldReturn400_Test() throws Exception {
        ItemDto badItem = ItemDto.builder().name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .isAvailable(null)
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItem)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void patchItem_shouldReturnItem_Test() throws Exception {
        when(itemClient.patchItem(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok().body(item));

        mockMvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemClient).patchItem(anyLong(), anyLong(), any());
    }

    @Test
    void patchItem_invalidUserId_shouldReturn400_Test() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void searchByText_shouldReturnItems_Test() throws Exception {
        when(itemClient.searchByText("query"))
                .thenReturn(ResponseEntity.ok().body(item));

        mockMvc.perform(get("/items/search?text=query"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemClient).searchByText("query");
    }

    @Test
    void postComment_shouldReturnComment_Test() throws Exception {
        when(itemClient.postComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok().body(comment));

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));

        verify(itemClient).postComment(anyLong(), anyLong(), any());
    }

    @Test
    void postComment_invalidUserId_shouldReturn400_Test() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void postComment_invalidItemId_shouldReturn400_Test() throws Exception {
        mockMvc.perform(post("/items/-1/comment")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }
}