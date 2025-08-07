package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
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
public class GatewayExceptionHandler {

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

        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage.toString())
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler(value = {ru.practicum.shareit.exception.ValidationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(Exception exception) {
        String errMessage = "Bad request";
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ApiError.builder()
                .error(errMessage)
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }
}
