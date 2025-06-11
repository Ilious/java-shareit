package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepo {

    Optional<User> findUserById(Long id);

    List<User> findAll();

    User addUser(User userDto);

    User updUser(User userDto);

    void removeUserById(Long id);

    boolean existsUserByEmail(String email);

    boolean existsUserByEmailAndIdNot(String email, Long id);

    User getUserById(Long id);
}
