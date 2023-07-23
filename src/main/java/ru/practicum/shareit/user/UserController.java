package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.api.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> postUser(@Valid @RequestBody UserDto userDto) {
        log.info("receive POST request for add new user with body={}", userDto);
        UserDto savedUser = userService.addUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,
                                              @PathVariable(value = "id") long userId) {
        log.info("receive PATCH request for update user with id={}, requestBody={}", userId, userDto);
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable(value = "id") long id
    ) {
        log.info("receive GET request for return user by id={}", id);

        UserDto userDto = userService.getUserById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        log.info("receive GET request for return all users");
        Collection<UserDto> usersDto = userService.getAllUsers();
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUserById(@PathVariable("id") Long id) {
        log.info("receive DELETE request fo delete user with id= {}", id);
        boolean result = userService.deleteUserById(id);
        if (!result) {
            log.warn("attempt to delete nonexistent user id={}", id);
            throw new NotFoundException(String.format("attempt to delete nonexistent user id=%s", id));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
