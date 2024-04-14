package ru.practicum.shareit.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query("update Booking b set b.status = ?2 where b.id = ?1")
  void updateBookingStatusById(Long id, String status);

  List<Booking> findBookingByBookerOrderByStart(Long bookerId);

  List<Booking> findBookingByBookerAndStatusOrderByStatus(Long bookerId, String status);

  @Query("select b from Booking b where b.booker = ?1 and b.status = 'APPROVED' "
      + "and current_timestamp between b.start and b.end")
  List<Booking> findCurrentBookingByBooker(Long bookerId);

  @Query("select b from Booking b where b.booker = ?1 and b.status = 'APPROVED' and current_timestamp > b.end")
  List<Booking> findPastBookingByBooker(Long bookerId);

  @Query("select b from Booking b where b.booker = ?1 and b.status = 'APPROVED' and current_timestamp < b.start")
  List<Booking> findFutureBookingByBooker(Long bookerId);

  boolean existsBookingByBookerAndStatus(Long bookerId, String status);

  List<Booking> findBookingsByItemId(Long itemId);
}
