package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.interfaces.IUserRepo;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepo implements IUserRepo {

    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public Optional<User> findUserById(Long id) {
        log.trace("UserRepo.findUserById: id {}", id);
        return Optional.ofNullable(storage.getOrDefault(id, null));
    }

    @Override
    public List<User> findAll() {
        log.trace("UserRepo.findAll");

        return new ArrayList<>(storage.values());
    }

    @Override
    public User addUser(User user) {
        log.trace("UserRepo.addUser: {}", user);
        user.setId(getNextIdx());

        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User updUser(User user) {
        log.trace("UserRepo.updUser: {}", user);
        storage.put(user.getId(), user);

        return user;
    }

    @Override
    public void removeUserById(Long id) {
        log.trace("UserRepo.removeUserById: id {}", id);
        storage.remove(id);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return storage.values().stream().anyMatch(
                u -> u.getEmail().equals(email)
        );
    }

    @Override
    public boolean existsUserByEmailAndIdNot(String email, Long id) {
        return storage.values().stream().anyMatch(
                u -> u.getEmail().equals(email) && !u.getId().equals(id)
        );
    }

    @Override
    public void validateId(Long id) {
        findUserById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User", "id", id)
                );
    }

    private Long getNextIdx() {
        return storage.keySet()
                .stream()
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }
}
