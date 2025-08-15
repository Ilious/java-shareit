package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long id) {
        log.info("Gateway sent request [getById]: id={}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Gateway sent request [getAll]: ");
        return userClient.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody @Validated(ValidateGroups.OnCreate.class) UserDto user) {
        log.info("Gateway sent request [createUser]: name={}", user.name());
        return userClient.postUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable @Positive Long id,
                                            @Validated(ValidateGroups.OnPatch.class) @RequestBody UserDto user) {
        log.info("Gateway sent request [patchUser]: id={}, name={}", id, user.name());
        return userClient.patchUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserById(@PathVariable @Positive Long id) {
        log.info("Gateway sent request [removeUserById]: id={}", id);
        userClient.removeById(id);
    }
}
