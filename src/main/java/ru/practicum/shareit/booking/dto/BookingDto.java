package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private final long id;
    private final Item item;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final User booker;
    private Status status;
}
