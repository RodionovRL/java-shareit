package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Validated
@Builder
public class BookingInputDto {
    private final long id;
    @NotNull
    @FutureOrPresent(message = "Start booking may be only in present or future")
    private final LocalDateTime start;
    @NotNull
    @Future(message = "End of booking may be only in future")
    private final LocalDateTime end;
    @NotNull
    private final long itemId;
}
