package ru.practicum.shareit.user.repository.api;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User newUser);

    User updateUser(long id, User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    boolean deleteUserById(Long id);
}
