package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.IUserService;
import ru.practicum.shareit.validator.ValidateGroups;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable @Positive Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Validated(ValidateGroups.OnCreate.class) UserDto user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable @Positive Long id,
                           @Validated(ValidateGroups.OnPatch.class) @RequestBody UserDto user) {
        return userService.patchUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserById(@PathVariable Long id) {
        userService.removeUserById(id);
    }
}
