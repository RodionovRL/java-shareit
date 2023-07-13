package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.api.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        checkUserIsExist(id);
        checkAlreadyExist(newUser, id);
        User oldUser = users.get(id);
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        if (newUser.getName()==null) {
            newUser.setName(oldUser.getName());
        }
        newUser.setId(id);
        return users.replace(id, newUser);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        checkUserIsExist(id);
        return users.get(id);
    }

    @Override
    public boolean deleteUserById(Long id) {
        return users.remove(id) != null;
    }
    private void checkUserIsExist(Long id) {
        if (!users.containsKey(id)) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new NotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
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

    private long getNewId() {
        long newId = ++ids;
        log.trace("создан новый userId = {}", newId);
        return newId;
    }
}
