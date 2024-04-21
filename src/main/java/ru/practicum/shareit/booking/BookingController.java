package ru.practicum.shareit.booking;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

  private final BookingService bookingService;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @PostMapping
  public BookingDTO book(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @Valid @RequestBody BookingRequestDTO body) {
    return bookingService.book(userId, body);
  }

  @PatchMapping("/{bookingId}")
  public BookingDTO changeBooking(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long ownerId,
      @PathVariable Long bookingId,
      @RequestParam Boolean approved
  ) {
    return bookingService.updateBooking(ownerId, bookingId, approved);
  }

  @GetMapping("/{bookingId}")
  public BookingDTO getById(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @PathVariable Long bookingId
  ) {
    return bookingService.getBooking(userId, bookingId);
  }

  @GetMapping
  public List<BookingDTO> getUserBookings(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @RequestParam(defaultValue = "ALL") RequestBookingStatus state,
      @RequestParam(required = false, defaultValue = "0") final Integer from,
      @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return bookingService.getBookingsByUser(userId, state, PageRequest.of(from, size));
  }

  @GetMapping("/owner")
  public List<BookingDTO> getUserItemBookings(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @RequestParam(defaultValue = "ALL") RequestBookingStatus state,
      @RequestParam(required = false, defaultValue = "0") final Integer from,
      @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return bookingService.getBookingStatusByOwner(userId, state, PageRequest.of(from, size));
  }

}
