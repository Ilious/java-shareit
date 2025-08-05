package ru.practicum.shareit.user;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {

    @Override
    List<User> findAll();

    void removeUserById(Long id);

    boolean existsUserByEmail(String email);

    boolean existsUserByEmailAndIdNot(String email, Long id);

    default User getUserById(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", id));
    }
}
