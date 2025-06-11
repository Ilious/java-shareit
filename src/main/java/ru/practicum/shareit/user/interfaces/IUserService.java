package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface IUserService {

    UserDto getUserById(Long id);

    List<UserDto> getAll();

    UserDto addUser(UserDto userDto);

    UserDto patchUser(Long id, UserDto userDto);

    void removeUserById(Long id);
}
