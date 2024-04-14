package ru.practicum.shareit.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Modifying
  @Query("update Booking b set b.status = ?2 where b.id = ?1")
  void updateBookingStatusById(Long id, String status);

  List<Booking> findBookingByBookerIdOrderByStartDesc(Long bookerId);

  List<Booking> findBookingByBookerIdAndStatusOrderByStartDesc(Long bookerId, String status);

  @Query("select b from Booking b where b.booker.id = ?1 "
      + "and current_timestamp between b.start and b.end "
      + "order by b.start DESC")
  List<Booking> findCurrentBookingByBookerId(Long bookerId);

  @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp > b.end "
      + "order by b.start DESC")
  List<Booking> findPastBookingByBookerId(Long bookerId);

  @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp < b.start "
      + "order by b.start DESC")
  List<Booking> findFutureBookingByBookerId(Long bookerId);

  boolean existsBookingByBookerIdAndStatus(Long bookerId, String status);

  List<Booking> findBookingsByItemId(Long itemId);

  @Query(value = "select b.*\n"
      + "from bookings b\n"
      + "         join items i on b.item_id = i.id\n"
      + "where i.user_id =?\n"
      + "order by b.start_date desc;", nativeQuery = true)
  List<Booking> findBookingByOwnerId(Long bookerId);

  @Query(value = "select b.*\n"
      + "from bookings b\n"
      + "         join items i on b.item_id = i.id\n"
      + "where i.user_id =?\n"
      + "and b.status = ?2 "
      + "order by b.start_date desc;", nativeQuery = true)
  List<Booking> findBookingByOwnerIdAndStatus(Long bookerId, String status);

  @Query(value = "select b.*\n"
      + "from bookings b\n"
      + "         join items i on b.item_id = i.id\n"
      + "where i.user_id =?\n"
      + "and current_timestamp > b.end_date "
      + "order by b.start_date desc;", nativeQuery = true)
  List<Booking> findPastBookingByOwnerId(Long bookerId);

  @Query(value = "select b.*\n"
      + "from bookings b\n"
      + "         join items i on b.item_id = i.id\n"
      + "where i.user_id =?\n"
      + "and current_timestamp between b.start_date and b.end_date  "
      + "order by b.start_date desc;", nativeQuery = true)
  List<Booking> findCurrentBookingByOwnerId(Long bookerId);

  @Query(value = "select b.*\n"
      + "from bookings b\n"
      + "         join items i on b.item_id = i.id\n"
      + "where i.user_id =?\n"
      + "and b.start_date > current_timestamp "
      + "order by b.start_date desc;", nativeQuery = true)
  List<Booking> findFutureBookingByOwnerId(Long bookerId);


}
