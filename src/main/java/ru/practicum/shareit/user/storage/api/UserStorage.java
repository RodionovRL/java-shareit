package ru.practicum.shareit.user.storage.api;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User newUser);

    User updateUser(long id, User user);

    Collection<User> getAllUsers();

    User getUserById(Long id);

    boolean deleteUserById(Long id);
}
