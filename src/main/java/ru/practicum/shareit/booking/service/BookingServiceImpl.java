package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.controller.State;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.api.BookingRepository;
import ru.practicum.shareit.booking.service.api.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingOutputDto   addBooking(BookingInputDto bookingInputDto, long bookerId) {
        validateBookingData(bookingInputDto);
        Item item = findItemById(bookingInputDto.getItemId());
        if (!item.getAvailable()) {
            log.error("BookingService: item with id={} not available", item.getId());
            throw new NotAvailableException(
                    String.format("Item with id=%s not available", item.getId()));
        }
        if (item.getOwner().getId().equals(bookerId)) {
            log.error("BookingService: bookerId={} equals ownerId of item with id={} ",bookerId, item.getId());
            throw new NotFoundException(
                    String.format("bookerId=%s equals ownerId of item with id=%s",bookerId, item.getId()));
        }
        User booker = findUserById(bookerId);
        Booking newBooking = bookingMapper.toBooking(bookingInputDto, item, booker);
        Booking addedBooking = bookingRepository.save(newBooking);
        log.info("bookingService: was add booking={}", addedBooking);
        return bookingMapper.toBookingOutputDto(addedBooking);
    }

    @Override
    public BookingOutputDto updateBooking(long bookingId, long ownerId, boolean isApproved) {
        findUserById(ownerId);
        Booking booking = findBookingById(bookingId);
        if (booking.getItem().getOwner().getId() != ownerId) {
            log.error("BookingService: only owner have access to item");
            throw new NotFoundException("only owner have access to item");
        }
        if (booking.getStatus().equals(Status.APPROVED) && isApproved) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "BookingService: booking with bookingId={} already Approved");
        }
        Status newStatus = isApproved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(newStatus);

        return bookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto getBookingByIdAndBookerId(long bookingId, long userId) {
        findUserById(userId);
        Booking booking = findBookingByIdAndUserId(bookingId, userId);
        log.info("bookingService: was returned booking={}, by id={}, userId={}", booking, bookingId, userId);
        return bookingMapper.toBookingOutputDto(booking);
    }

    @Override
    public List<BookingOutputDto> getAllUsersBookings(Long bookerId, State state) {
        findUserById(bookerId);
        List<Booking> allUsersBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdOrderByIdDesc(bookerId);
                break;
            case PAST:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndEndBeforeOrderByIdDesc(bookerId, now);
                break;
            case FUTURE:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStartAfterOrderByIdDesc(bookerId, now);
                break;
            case CURRENT:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStartAfterAndEndBeforeOrderByIdDesc(bookerId,
                                now,
                                now);
                break;
            case WAITING:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStatusOrderByIdDesc(bookerId, Status.WAITING);
                break;
            case REJECTED:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStatusOrderByIdDesc(bookerId, Status.REJECTED);
                break;
        }
        List<BookingOutputDto> allBookingsDto = bookingMapper.map(allUsersBookings);
        log.info("bookingService: was returned all {} bookings for bookerId={}", allBookingsDto.size(), bookerId);
        return allBookingsDto;
    }

    @Override
    public List<BookingOutputDto> getAllOwnersBookings(Long ownerId, State state) {
        findUserById(ownerId);
        List<Booking> allUsersBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdOrderByIdDesc(ownerId);
                break;
            case PAST:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByIdDesc(ownerId, now);
                break;
            case FUTURE:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByIdDesc(ownerId, now);
                break;
            case CURRENT:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndBeforeOrderByIdDesc(ownerId,
                                now,
                                now);
                break;
            case WAITING:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByIdDesc(ownerId, Status.WAITING);
                break;
            case REJECTED:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByIdDesc(ownerId, Status.REJECTED);
                break;
        }
        List<BookingOutputDto> allBookingsDto = bookingMapper.map(allUsersBookings);
        log.info("bookingService: was returned all {} bookings for ownerId={}", allBookingsDto.size(), ownerId);
        return allBookingsDto;
    }

    private Booking findBookingByIdAndUserId(Long bookingId, Long userId) {
        Booking booking = findBookingById(bookingId);
        if (booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        }
        throw new NotFoundException(String.format("booking with id=%s with (bookerId or ownerId)=%s not found",
                bookingId,
                userId));
    }

    private void validateBookingData(BookingInputDto bookingInputDto) {
        if (!bookingInputDto.getEnd().isAfter(bookingInputDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date must be after start date");
        }
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%s not found", userId)));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("item with id=%s not found", itemId)));
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("booking with id=%s not found", bookingId)));
    }

    private void checkAccess(Long userId, Booking booking) {
        if (booking.getBooker().getId().equals(userId)) {
            return;
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            return;
        }
        throw new NotOwnerException("only owner or booker have access to booking");
    }
}
