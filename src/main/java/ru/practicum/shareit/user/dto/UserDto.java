package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String name;
    @NotNull(message = "email must be not null")
    @Email(message = "it's not email")
    private String email;
}
