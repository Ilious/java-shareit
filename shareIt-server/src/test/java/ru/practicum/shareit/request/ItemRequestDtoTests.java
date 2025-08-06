package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTests {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private ItemRequestDto item;

    @Test
    void testDateTimeFormat() throws IOException {
        LocalDateTime dt = LocalDateTime.of(2019, 10, 20, 12, 30);

        item = ItemRequestDto.builder()
                .created(dt)
                .build();

        JsonContent<ItemRequestDto> result = json.write(item);

        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2019-10-20T12:30:00");
    }

    @Test
    void testValidation() {
        Set<ConstraintViolation<ItemRequestDto>> violations;
        item = ItemRequestDto.builder()
                .description("    ")
                .created(LocalDateTime.now().plusMinutes(1)).build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(item, ValidateGroups.OnCreate.class);
        }

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }
}
