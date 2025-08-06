package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTests {

    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto booking;

    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        now = LocalDateTime.now();
    }

    @Test
    void testDateTimeFormat() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 8, 6, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 6, 12, 0);

        BookingDto booking = BookingDto.builder()
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-08-06T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-08-06T12:00:00");
    }


    @Test
    void testValidation() {
        Set<ConstraintViolation<BookingDto>> violations;
        booking = BookingDto.builder().start(now.minusMinutes(1)).end(now).build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(booking, ValidateGroups.OnCreate.class);
        }

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2);
    }
}
