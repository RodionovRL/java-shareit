package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.api.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static long ids;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User newUser) {
        checkAlreadyExist(newUser);
        long id = getNewId();
        newUser.setId(id);
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public User updateUser(long id, User newUser) {
        User oldUser = users.get(id);
        if (oldUser == null) {
            log.error("user with id={} not found", id);
            throw new NotFoundException(String.format(
                    "user with id=%s not found", id));
        }

        checkAlreadyExist(newUser, id);

        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        newUser.setId(id);
        return users.replace(id, newUser);
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.error("user with id={} not found", id);
            throw new NotFoundException(String.format(
                    "user with id=%s not found", id));
        }
        return user;
    }

    @Override
    public boolean deleteUserById(Long id) {
        return users.remove(id) != null;
    }

    private void checkAlreadyExist(User newUser) {
        if (getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            log.error("user with email={} already exist", newUser.getEmail());
            throw new AlreadyExistException(String.format(
                    "user with email=%s already exist", newUser.getEmail()));
        }
    }

    private void checkAlreadyExist(User newUser, long id) {
        if (getAllUsers().stream()
                .filter(u -> u.getId() != id)
                .anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            log.error("user with email={} already exist with another id", newUser.getEmail());
            throw new AlreadyExistException(String.format(
                    "user with email=%s already exist", newUser.getEmail()));
        }
    }

    private static long getNewId() {
        long newId = ++ids;
        log.trace("created new userId={}", newId);
        return newId;
    }
}
