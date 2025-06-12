package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.IUserRepo;
import ru.practicum.shareit.user.interfaces.IUserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepo userRepo;

    @Override
    public UserDto getUserById(Long id) {
        log.debug("UserService.getUserById: id {}", id);
        return UserMapper.toDto(getUser(id));
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("UserService.getAll: started");
        List<User> users = userRepo.findAll();
        if (users.isEmpty())
            throw new EntityNotFoundException("Users", "storage", "any");

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        log.debug("UserService.addUser: {}", userDto);
        validateEmail(userDto.email(), null);

        User entity = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userRepo.addUser(entity));
    }

    @Override
    public UserDto patchUser(Long id, UserDto userDto) {
        log.debug("UserService.patchUser: id {}, userDto {}", id, userDto);
        validateEmail(userDto.email(), id);
        User userById = getUser(id);
        UserMapper.patchFields(userById, userDto);

        return UserMapper.toDto(userRepo.updUser(userById));
    }

    @Override
    public void removeUserById(Long id) {
        log.debug("UserService.removeUserById: {}", id);
        userRepo.removeUserById(id);
    }

    private User getUser(Long id) {
        return userRepo.findUserById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User", "id", id)
                );
    }

    private void validateEmail(String email, Long id) {
        boolean userByEmailExists = Objects.isNull(id) ?
                userRepo.existsUserByEmail(email) :
                userRepo.existsUserByEmailAndIdNot(email, id);

        if (userByEmailExists)
            throw new EntityAlreadyExistsException("User", "email", email);
    }
}
