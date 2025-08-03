package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.api.ApiError;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {

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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        StringBuilder errMessage = new StringBuilder("Input fields aren't correct: ");
        String errorFields = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        errMessage.append(errorFields);

        log.warn("{}\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage.toString())
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        String errMessage = String.format(
                "Required header [%s] is missing",
                exception.getHeaderName()
        );
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationsException(ConstraintViolationException exception) {
        StringBuilder errMessage = new StringBuilder("Input fields aren't correct: ");
        String errorFields = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        errMessage.append(errorFields);

        log.warn("{}\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage.toString())
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler({InternalServerException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnknownException(Exception exception) {
        String errMessage = "Error on server";
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }
}
