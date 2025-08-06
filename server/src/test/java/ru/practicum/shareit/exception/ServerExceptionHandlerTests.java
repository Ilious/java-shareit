package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.api.ApiError;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ServerExceptionHandlerTests {

    @InjectMocks
    private ServerExceptionHandler serverExceptionHandler;

    @Test
    void handleForbiddenException_Test() {
        ForbiddenException exception = new ForbiddenException(1L);

        ApiError result = serverExceptionHandler.handleForbiddenException(exception);

        assertEquals("Forbidden err", result.error());
        assertEquals(403, result.code());

    }

    @Test
    void handleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Item", "id", 1);

        ApiError result = serverExceptionHandler.handleEntityNotFoundException(exception);

        assertEquals("Entity [Item] not found by [id]: 1", result.error());
        assertEquals(404, result.code());
    }

    @Test
    void handleEntityAlreadyExistsException() {
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Item", "id", 1);

        ApiError result = serverExceptionHandler.handleEntityAlreadyExistsException(exception);

        assertEquals("Entity [Item] already exists by [id]: 1", result.error());
        assertEquals(409, result.code());
    }

    @Test
    void handleValidationException() {
        BadRequestException exception = new BadRequestException("bad rquest");

        ApiError result = serverExceptionHandler.handleValidationException(exception);

        assertEquals("Bad request", result.error());
        assertEquals(400, result.code());
    }

    @Test
    void handleUnknownException() {
        NullPointerException exception = new NullPointerException("Error on server");

        ApiError result = serverExceptionHandler.handleUnknownException(exception);

        assertEquals("Error on server", result.error());
        assertEquals(500, result.code());
    }
}