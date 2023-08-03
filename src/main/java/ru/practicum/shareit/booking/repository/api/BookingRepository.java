package ru.practicum.shareit.booking.repository.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByIdDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByIdDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByIdDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStartAfterAndEndBeforeOrderByIdDesc(Long bookerId,
                                                                           LocalDateTime now1,
                                                                           LocalDateTime now2);

    List<Booking> findAllByBooker_IdAndStatusOrderByIdDesc(Long bookerId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByIdDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByIdDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByIdDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterAndEndBeforeOrderByIdDesc(Long ownerId,
                                                                             LocalDateTime now1,
                                                                             LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByIdDesc(Long ownerId, Status status);

    @Query(value = "SELECT DISTINCT ON(item_id) * FROM bookings b " +
            "WHERE b.item_id IN :itemIds " +
            "AND b.status = :status " +
            "AND b.start_date < :date " +
            "ORDER BY b.item_id, (b.start_date) DESC;", nativeQuery = true)
    List<Booking> findLastBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("status") String status,
                                           @Param("date") LocalDateTime date);

    @Query(value = "SELECT DISTINCT ON(item_id) * FROM bookings b " +
            "WHERE b.item_id IN :itemIds " +
            "AND b.status = :status " +
            "AND b.start_date > :date " +
            "ORDER BY b.item_id, (b.start_date) ;", nativeQuery = true)
    List<Booking> findNextBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("status") String status,
                                           @Param("date") LocalDateTime date);
}
