package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private static Faker faker;

    private UserDto user;

    private String username;

    private String email;

    @BeforeAll
    static void init() {
        faker = new Faker();
    }

    @BeforeEach
    void setUp() {
        username = faker.name().username();
        email = faker.internet().emailAddress();
        user = UserDto.builder()
                .name(username)
                .email(email)
                .build();
    }

    @Test
    void testValidation() {
        Set<ConstraintViolation<UserDto>> violations;
        user = UserDto.builder().name("    ").email(null).build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(user, ValidateGroups.OnCreate.class);
        }

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2);

        user = UserDto.builder().name(username).email("fake.mail.ru").build();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(user, ValidateGroups.OnCreate.class);
        }

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }
}
