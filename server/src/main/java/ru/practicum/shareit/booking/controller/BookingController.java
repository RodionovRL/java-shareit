package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.api.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String DEFAULT_SIZE = "25";
    private static final String DEFAULT_FROM = "0";

    @PostMapping()
    public ResponseEntity<BookingOutputDto> addBooking( @RequestBody BookingInputDto bookingInputDto,
                                                       @RequestHeader(value = "X-Sharer-User-Id") long bookerId
    ) {
        log.info("BookingController: receive POST request for add new booking with bookerId={}, body={}",
                bookerId,
                bookingInputDto);
        BookingOutputDto savedBooking = bookingService.addBooking(bookingInputDto, bookerId);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<BookingOutputDto> patchBooking(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                                  @PathVariable(value = "bookingId") long bookingId,
                                                  @RequestParam(value = "approved") boolean isApproved
    ) {
        log.info("receive Patch request for patch booking id={}, ownerId={}, approved={}",
                bookingId, ownerId, isApproved);

        BookingOutputDto patchedBookingDto = bookingService.updateBooking(bookingId, ownerId, isApproved);
        return new ResponseEntity<>(patchedBookingDto, HttpStatus.OK);
    }

    @GetMapping("")
    ResponseEntity<List<BookingOutputDto>> getAllUsersBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") State stateParam,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM)  int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE)  int size
    ) {
        log.info("receive GET request for return all bookings for bookerId={}, state={}, from={}, size={}",
                bookerId, stateParam, from, size);
        List<BookingOutputDto> bookingOutputDto = bookingService.getAllUsersBookings(bookerId, stateParam, from, size);
        return new ResponseEntity<>(bookingOutputDto, HttpStatus.OK);
    }


    @GetMapping("/{bookingId}")
    ResponseEntity<BookingOutputDto> getBooking(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                @PathVariable(value = "bookingId") long bookingId
    ) {
        log.info("receive GET request for return booking by id={}, userId={}", bookingId, userId);

        BookingOutputDto bookingOutputDto = bookingService.getBookingByIdAndBookerId(bookingId, userId);
        return new ResponseEntity<>(bookingOutputDto, HttpStatus.OK);
    }

    @GetMapping("/owner")
    ResponseEntity<List<BookingOutputDto>> getAllOwnersBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") State stateParam,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE)  int size
    ) {
        log.info("receive GET request for return all bookings for owner={}, state={}, from={}, size={}",
                ownerId, stateParam, from, size);
        List<BookingOutputDto> bookingsOutputDto = bookingService.getAllOwnersBookings(ownerId, stateParam, from, size);
        return new ResponseEntity<>(bookingsOutputDto, HttpStatus.OK);
    }
}