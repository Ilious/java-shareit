package ru.practicum.shareit.item;

import com.github.javafaker.Faker;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTests {

    @Autowired
    private JacksonTester<ItemDto> jsonItem;

    @Autowired
    private JacksonTester<CommentDto> jsonComment;


    private ItemDto item;

    private CommentDto comment;

    private static Faker faker;

    @BeforeAll
    static void init() {
        faker = new Faker();
    }

    @BeforeEach
    void setUp() {
        item = ItemDto.builder()
                .name(faker.commerce().productName())
                .description(faker.commerce().material())
                .isAvailable(faker.bool().bool())
                .build();
    }

    @Test
    void testDateTimeFormat() throws IOException {
        LocalDateTime dt = LocalDateTime.of(2019, 10, 20, 12, 30);

        comment = CommentDto.builder()
                .created(dt)
                .build();

        JsonContent<CommentDto> result = jsonComment.write(comment);

        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2019-10-20T12:30:00");
    }

    @Test
    void testValidation() {
        Set<ConstraintViolation<ItemDto>> violations;
        item = ItemDto.builder().name("     ").description("    ").isAvailable(null).build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(item, ValidateGroups.OnCreate.class);
        }

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(3);
    }

    @Test
    void testValidationComment() {
        Set<ConstraintViolation<CommentDto>> violations;
        comment = CommentDto.builder().text(null).authorName("    ")
                .created(LocalDateTime.now()
                        .plusMinutes(1)).build();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(comment, ValidateGroups.OnCreate.class);
        }

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(3);
    }
}
