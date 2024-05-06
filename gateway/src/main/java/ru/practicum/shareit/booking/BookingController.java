package ru.practicum.shareit.booking;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

  private final BookingClient bookingClient;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @GetMapping
  public ResponseEntity<Object> getBookings(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @RequestParam(name = "state", defaultValue = "all") String stateParam,
      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
  ) {
    BookingState state = BookingState.from(stateParam)
        .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
    int page = from > 0 ? from / size : from;
    log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, page,
        size);
    return bookingClient.getBookings(userId, state, page, size);
  }

  @GetMapping("/owner")
  public ResponseEntity<Object> getUserItemBookings(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @RequestParam(name = "state", defaultValue = "all") String stateParam,
      @PositiveOrZero @RequestParam(required = false, defaultValue = "0") final Integer from,
      @Positive @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    BookingState state = BookingState.from(stateParam)
        .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
    log.info("Get owner booking with state {}, userId={}, from={}, size={}", stateParam, userId,
        from,
        size);
    return bookingClient.getBookingStatusByOwner(userId, state, from, size);
  }

  @PostMapping
  public ResponseEntity<Object> bookItem(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @RequestBody @Valid BookItemRequestDto requestDto) {
    log.info("Creating booking {}, userId={}", requestDto, userId);
    return bookingClient.bookItem(userId, requestDto);
  }

  @PatchMapping("/{bookingId}")
  public ResponseEntity<Object> changeBooking(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long ownerId,
      @PathVariable Long bookingId,
      @RequestParam Boolean approved
  ) {
    log.info("PATCH /bookings/{bookingId} with params: booking={}, userId={}, approved={}",
        bookingId, ownerId, approved);
    return bookingClient.updateBooking(ownerId, bookingId, approved);
  }

  @GetMapping("/{bookingId}")
  public ResponseEntity<Object> getBooking(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @PathVariable Long bookingId) {
    log.info("Get booking {}, userId={}", bookingId, userId);
    return bookingClient.getBooking(userId, bookingId);
  }

}