package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;

public interface BookingService {

  BookingDTO book(Long bookerId, BookingRequestDTO bookingParam);

  BookingDTO updateBooking(Long ownerId, Long bookingId, Boolean isApproved);

  BookingDTO getBooking(Long userId, Long bookingId);

  List<BookingDTO> getBookingsByUser(Long bookerId, RequestBookingStatus state);

  List<BookingDTO> getBookingStatusByOwner(Long ownerId, RequestBookingStatus state);

}
