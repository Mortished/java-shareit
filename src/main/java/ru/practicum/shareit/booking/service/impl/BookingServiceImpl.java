package ru.practicum.shareit.booking.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  @Override
  public BookingDTO book(Long bookerId, BookingRequestDTO bookingParam) {
    Optional<User> user = userRepository.findById(bookerId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(bookerId.toString());
    }
    Optional<Item> item = itemRepository.findById(bookingParam.getItemId());
    if (item.isEmpty()) {
      throw new ItemNotFoundException(bookingParam.getItemId().toString());
    }
    bookingParam.setStatus(BookingStatus.WAITING.name());
    Booking result = bookingRepository.save(BookingMapper.toBooking(bookingParam, user.get(), item.get()));

    return BookingMapper.toBookingDTO(result);
  }

  @Override
  public BookingDTO updateBooking(Long ownerId, Long bookingId, Boolean isApproved) {
    Optional<User> user = userRepository.findById(ownerId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(ownerId.toString());
    }
    Optional<Booking> booking = bookingRepository.findById(bookingId);
    if (booking.isEmpty()) {
      throw new BookingNotFoundException(bookingId.toString());
    }
    Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
    if (item.isEmpty()) {
      throw new ItemNotFoundException(booking.get().getItem().getId().toString());
    }
    if (!ownerId.equals(item.get().getOwner().getId())) {
      throw new UserNotFoundException(ownerId.toString());
    }

    return null;
  }

  @Override
  public BookingDTO getBooking(Long userId, Long bookingId) {
    return null;
  }

  @Override
  public List<BookingDTO> getBookingsByUser(Long bookerId, RequestBookingStatus state) {
    return null;
  }

  @Override
  public List<BookingDTO> getBookingStatusByOwner(Long ownerId, RequestBookingStatus state) {
    return null;
  }
}
