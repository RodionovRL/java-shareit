package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class User {
    private long id;
    @NotNull(message = "name must be not null")
    private String name;
    @Email(message = "it's not email")
    @NotNull(message = "email must be not null")
    private String email;
}
