package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.IUserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Server received request [getById]: id={}", id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Server received request [getAll]: ");
        return userService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("Server received request [createUser]: name={}", user.name());
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable Long id,
                             @RequestBody UserDto user) {
        log.info("Server received request [patchUser]: id={}, name={}", id, user.name());
        return userService.patchUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserById(@PathVariable Long id) {
        log.info("Server received request [removeUserById]: id={}", id);
        userService.removeUserById(id);
    }
}
