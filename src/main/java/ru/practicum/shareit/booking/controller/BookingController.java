package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.api.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public ResponseEntity<BookingOutputDto> addBooking(@Valid @RequestBody BookingInputDto bookingInputDto,
                                                       @RequestHeader(value = "X-Sharer-User-Id") long bookerId
    ) {
        log.info("BookingController: receive POST request for add new booking with bookerId={}, body={}",
                bookerId,
                bookingInputDto);
        BookingOutputDto savedBooking = bookingService.addBooking(bookingInputDto, bookerId);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<BookingOutputDto> patchBooking(@PathVariable(value = "bookingId") long bookingId,
                                                  @RequestParam(value = "approved") boolean isApproved,
                                                  @RequestHeader(value = "X-Sharer-User-Id") long ownerId) {
        BookingOutputDto patchedBookingDto = bookingService.updateBooking(bookingId, ownerId, isApproved);
        return new ResponseEntity<>(patchedBookingDto, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<BookingOutputDto> getBooking(@PathVariable(value = "bookingId") long bookingId,
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("receive GET request for return booking by id={}, userId={}", bookingId, userId);

        BookingOutputDto bookingOutputDto = bookingService.getBookingByIdAndBookerId(bookingId, userId);
        return new ResponseEntity<>(bookingOutputDto, HttpStatus.OK);
    }

    @GetMapping()
    ResponseEntity<List<BookingOutputDto>> getAllUsersBooking(@RequestHeader(value = "X-Sharer-User-Id") long bookerId,
                                                              @RequestParam(value = "state",
                                                                      defaultValue = "ALL") String stateParam) {

        log.info("receive GET request for return all bookings for bookerId={}, state={}", bookerId, stateParam);
        State state;
        try {
            state = State.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            log.error("Unknown state: {}", stateParam);
            throw new IllegalArgumentException(String.format("Unknown state: %s", stateParam));
        }
        List<BookingOutputDto> bookingOutputDto = bookingService.getAllUsersBookings(bookerId, state);
        return new ResponseEntity<>(bookingOutputDto, HttpStatus.OK);
    }


    @GetMapping("/owner")
    ResponseEntity<List<BookingOutputDto>> getAllOwnersBooking(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                                               @RequestParam(value = "state",
                                                                       defaultValue = "ALL") String stateParam) {
        log.info("receive GET request for return all bookings for owner={}, state={}", ownerId, stateParam);
        State state;
        try {
            state = State.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            log.error("Unknown state: UNSUPPORTED_STATUS={}", stateParam);
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }

        List<BookingOutputDto> bookingsOutputDto = bookingService.getAllOwnersBookings(ownerId, state);
        return new ResponseEntity<>(bookingsOutputDto, HttpStatus.OK);
    }
}