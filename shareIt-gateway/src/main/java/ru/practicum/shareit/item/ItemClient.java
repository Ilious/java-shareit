package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getById(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getByAllUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> postItem(long userId, ItemDto dto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(dto);
            log.info("Sending request [postItem]: {}", jsonRequest);
        } catch (JsonProcessingException e) {
            log.error("Error logging request body", e);
        }

        return post("", userId, dto);
    }

    public ResponseEntity<Object> patchItem(long itemId, long userId, ItemDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> searchByText(String text) {
        Map<String, Object> params = Map.of(
                "text", text
        );
        return get("/search?text={text}", params);
    }

    public ResponseEntity<Object> postComment(long userId, long itemId, CommentDto dto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/{itemId}/comment", userId, parameters, dto);
    }
}
