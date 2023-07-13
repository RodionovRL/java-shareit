package ru.practicum.shareit.user.service.api;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto newUserDto);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();

    boolean deleteUserById(Long id);
}
