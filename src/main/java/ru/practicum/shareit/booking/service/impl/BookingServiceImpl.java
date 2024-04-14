package ru.practicum.shareit.booking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotAvalibleException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.RequestStatusException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    validateDate(bookingParam);
    Optional<User> user = userRepository.findById(bookerId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(bookerId.toString());
    }
    Optional<Item> item = itemRepository.findById(bookingParam.getItemId());
    if (item.isEmpty()) {
      throw new ItemNotFoundException(bookingParam.getItemId().toString());
    }
    if (bookerId.equals(item.get().getOwner().getId())) {
      throw new UserNotFoundException(bookerId.toString());
    }
    if (!itemRepository.isItemAvalible(bookingParam.getItemId())) {
      throw new ItemNotAvalibleException(bookingParam.getItemId().toString());
    }

    bookingParam.setStatus(BookingStatus.WAITING.name());

    Booking result = bookingRepository.save(
        BookingMapper.toBooking(bookingParam, user.get(), item.get()));
    return BookingMapper.toBookingDTO(result);
  }


  @Override
  @Transactional(propagation = Propagation.NESTED)
  public BookingDTO updateBooking(Long ownerId, Long bookingId, Boolean isApproved) {
    Optional<User> user = userRepository.findById(ownerId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(ownerId.toString());
    }
    Optional<Booking> booking = bookingRepository.findById(bookingId);
    if (booking.isEmpty()) {
      throw new BookingNotFoundException(bookingId.toString());
    }
    if (BookingStatus.valueOf(booking.get().getStatus()).equals(BookingStatus.APPROVED)) {
      throw new ValidateException();
    }
    Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
    if (item.isEmpty()) {
      throw new ItemNotFoundException(booking.get().getItem().getId().toString());
    }
    if (!ownerId.equals(item.get().getOwner().getId())) {
      throw new UserNotFoundException(ownerId.toString());
    }
    if (isApproved) {
      itemRepository.updateItemAvailableById(item.get().getId(), isApproved);
      bookingRepository.updateBookingStatusById(bookingId, BookingStatus.APPROVED.name());
    } else {
      bookingRepository.updateBookingStatusById(bookingId, BookingStatus.REJECTED.name());
    }

    return BookingMapper.toBookingDTO(bookingRepository.findById(bookingId).get());
  }

  @Override
  public BookingDTO getBooking(Long userId, Long bookingId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(userId.toString());
    }
    Optional<Booking> booking = bookingRepository.findById(bookingId);
    if (booking.isEmpty()) {
      throw new BookingNotFoundException(bookingId.toString());
    }
    Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
    if (item.isEmpty()) {
      throw new ItemNotFoundException(booking.get().getItem().getId().toString());
    }
    if (!userId.equals(item.get().getOwner().getId())
        && !userId.equals(booking.get().getBooker().getId())) {
      throw new UserNotFoundException(userId.toString());
    }
    return BookingMapper.toBookingDTO(booking.get());
  }

  @Override
  public List<BookingDTO> getBookingsByUser(Long bookerId, RequestBookingStatus state) {
    Optional<User> user = userRepository.findById(bookerId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(bookerId.toString());
    }
    List<Booking> result = findBookingsByUserIdAndStatus(bookerId, state);
    return result.stream()
        .map(BookingMapper::toBookingDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<BookingDTO> getBookingStatusByOwner(Long ownerId, RequestBookingStatus state) {
    Optional<User> user = userRepository.findById(ownerId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(ownerId.toString());
    }
    List<Item> items = itemRepository.findItemsByOwnerId(ownerId);
    if (items.isEmpty()) {
      throw new ItemNotFoundException(ownerId.toString());
    }
    return findBookingsByOwnerIdAndStatus(ownerId, state).stream()
        .map(BookingMapper::toBookingDTO)
        .collect(Collectors.toList());
  }

  private List<Booking> findBookingsByOwnerIdAndStatus(Long ownerId, RequestBookingStatus state) {
    switch (state) {
      case ALL:
        return bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(ownerId);
      case WAITING:
        return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
            BookingStatus.WAITING.name());
      case REJECTED:
        return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
            BookingStatus.REJECTED.name());
      case CURRENT:
        return bookingRepository.findCurrentBookingByOwnerId(ownerId);
      case PAST:
        return bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
      case FUTURE:
        return bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
      default:
        throw new RequestStatusException(state.name());
    }
  }

  private List<Booking> findBookingsByUserIdAndStatus(Long bookerId, RequestBookingStatus state) {
    switch (state) {
      case ALL:
        return bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId);
      case WAITING:
        return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
            BookingStatus.WAITING.name());
      case REJECTED:
        return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
            BookingStatus.REJECTED.name());
      case CURRENT:
        return bookingRepository.findCurrentBookingByBookerId(bookerId);
      case PAST:
        return bookingRepository.findPastBookingByBookerId(bookerId);
      case FUTURE:
        return bookingRepository.findFutureBookingByBookerId(bookerId);
      default:
        throw new RequestStatusException(state.name());
    }
  }

  private void validateDate(BookingRequestDTO bookingParam) {
    if (bookingParam.getStart().equals(bookingParam.getEnd())
        || bookingParam.getStart().isAfter(bookingParam.getEnd())
    ) {
      throw new ValidateException();
    }
  }

}
