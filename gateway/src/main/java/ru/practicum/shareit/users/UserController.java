package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.users.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("GW UserController: receive POST request for add new user with body={}", userDto);
        return userClient.addUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "id") long userId) {
        log.info("GW UserController: receive GET request for return user by userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                                              @PathVariable(value = "id") long userId) {
        log.info("GW UserController: receive PATCH request for update user with id={}, requestBody={}",
                userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GW UserController: receive GET request for return all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("id") Long userId) {
        log.info("GW UserController: receive DELETE request fo delete user with userId= {}", userId);
        return userClient.deleteUserById(userId);
    }
}
