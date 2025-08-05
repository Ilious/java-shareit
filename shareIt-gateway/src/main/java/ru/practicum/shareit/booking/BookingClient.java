package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getByUser(long userId, BookingState state) {
        Map<String, Object> params = Map.of(
                "state", state.name()
        );

        return get("?state={state}", userId, params);
    }

    public ResponseEntity<Object> getByOwner(long userId, BookingState state) {
        Map<String, Object> params = Map.of(
                "state", state.name()
        );

        return get("/owner?state={state}", userId, params);
    }

    public ResponseEntity<Object> postBooking(long userId, BookingDto requestDto) {
        return post("", userId, requestDto);
    }


    public ResponseEntity<Object> patchBooking(long userId, Long bookingId, Boolean approved) {
        Map<String, Object> params = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        return patch("/{bookingId}?approved={approved}", userId, params, null);
    }
}
