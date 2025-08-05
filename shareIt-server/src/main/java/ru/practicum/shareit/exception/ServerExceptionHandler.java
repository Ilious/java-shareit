package ru.practicum.shareit.exception;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.api.ApiError;

@Slf4j
@RestControllerAdvice
public class ServerExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleUnauthorizedException(ForbiddenException exception) {
        String errMessage = "Forbidden err";
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.FORBIDDEN.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(EntityNotFoundException exception) {
        String errMessage = String.format(
                "Entity [%s] not found by [%s]: %s",
                exception.getEntity(), exception.getField(), exception.getValue()
        );
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.NOT_FOUND.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEntityAlreadyExistsException(EntityAlreadyExistsException exception) {
        String errMessage = String.format(
                "Entity [%s] already exists by [%s]: %s",
                exception.getEntity(), exception.getField(), exception.getValue()
        );
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.CONFLICT.value()).build();
    }

    @ExceptionHandler(value = { ValidationException.class, BadRequestException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(Exception exception) {
        String errMessage = "Bad request";
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnknownException(Exception exception) {
        String errMessage = "Error on server";
        log.warn("{}: {} \n {}", errMessage, exception.getMessage(), exception.getStackTrace());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }
}
