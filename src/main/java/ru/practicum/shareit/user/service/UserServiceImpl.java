package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.api.UserService;
import ru.practicum.shareit.user.storage.api.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
private final UserStorage userStorage;
private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto newUserDto) {
        User addedUser = userStorage.addUser(userMapper.toUser(newUserDto));
        log.info("userService: was add user={}", addedUser);
        return userMapper.toUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
//        checkName(userDto);
        User user = userMapper.toUser(userDto);
        User oldUser = userStorage.updateUser(userId, user);
        log.info("userService: user={} change to user={}", oldUser, user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userStorage.getUserById(id);
        log.info("userService: was returned user={}, by id={}", user, id);
        return userMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> allUsers = userStorage.getAllUsers();
        log.info("userService: returned all {} users", allUsers.size());
        return userMapper.map(allUsers);
    }

    @Override
    public boolean deleteUserById(Long id) {
        return userStorage.deleteUserById(id);
    }

}
