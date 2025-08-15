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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTests {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static Faker faker;

    private static ObjectMapper objectMapper;

    private ItemDto item;

    private ItemDto item2;

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

        item2 = ItemDto.builder()
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
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemService).getItemById(anyLong());
    }

    @Test
    void getAllByUserId_shouldReturnItems_Test() throws Exception {
        when(itemService.getAllByUserId(anyLong()))
                .thenReturn(List.of(item, item2));

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(item, item2))));

        verify(itemService).getAllByUserId(anyLong());
    }

    @Test
    void postItem_shouldReturnItem_Test() throws Exception {
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemService).addItem(anyLong(), any());
    }

    @Test
    void patchItem_shouldReturnItem_Test() throws Exception {
        when(itemService.patchItem(anyLong(), any(), anyLong()))
                .thenReturn(item);

        mockMvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemService).patchItem(anyLong(), any(), anyLong());
    }

    @Test
    void searchByText_shouldReturnItems_Test() throws Exception {
        when(itemService.searchByTextQuery(anyString()))
                .thenReturn(List.of(item, item2));

        mockMvc.perform(get("/items/search?text=query"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(item, item2))));

        verify(itemService).searchByTextQuery(anyString());
    }

    @Test
    void postComment_shouldReturnComment_Test() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));

        verify(itemService).addComment(anyLong(), anyLong(), any());
    }

}