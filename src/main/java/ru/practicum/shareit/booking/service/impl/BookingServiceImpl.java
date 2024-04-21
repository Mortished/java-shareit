package ru.practicum.shareit.booking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
  private final EntityManager entityManager;

  @Override
  public BookingDTO book(Long bookerId, BookingRequestDTO bookingParam) {
    validateDate(bookingParam);
    User user = userRepository.findById(bookerId)
        .orElseThrow(() -> new UserNotFoundException(bookerId.toString()));

    Item item = itemRepository.findById(bookingParam.getItemId())
        .orElseThrow(() -> new ItemNotFoundException(bookingParam.getItemId().toString()));

    if (bookerId.equals(item.getOwner().getId())) {
      throw new UserNotFoundException(bookerId.toString());
    }
    if (!itemRepository.isItemAvalible(bookingParam.getItemId())) {
      throw new ItemNotAvalibleException(bookingParam.getItemId().toString());
    }

    bookingParam.setStatus(BookingStatus.WAITING.name());

    Booking result = bookingRepository.save(
        BookingMapper.toBooking(bookingParam, user, item));
    return BookingMapper.toBookingDTO(result);
  }


  @Override
  @Transactional
  public BookingDTO updateBooking(Long ownerId, Long bookingId, Boolean isApproved) {
    User user = userRepository.findById(ownerId)
        .orElseThrow(() -> new UserNotFoundException(ownerId.toString()));

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new BookingNotFoundException(bookingId.toString()));

    if (BookingStatus.valueOf(booking.getStatus()).equals(BookingStatus.APPROVED)) {
      throw new ValidateException();
    }
    Item item = itemRepository.findById(booking.getItem().getId())
        .orElseThrow(() -> new ItemNotFoundException(booking.getItem().getId().toString()));

    if (!ownerId.equals(item.getOwner().getId())) {
      throw new UserNotFoundException(ownerId.toString());
    }
    if (isApproved) {
      itemRepository.updateItemAvailableById(item.getId(), isApproved);
      bookingRepository.updateBookingStatusById(bookingId, BookingStatus.APPROVED.name());
    } else {
      bookingRepository.updateBookingStatusById(bookingId, BookingStatus.REJECTED.name());
    }
    var result = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new BookingNotFoundException(bookingId.toString()));

    entityManager.refresh(result);
    return BookingMapper.toBookingDTO(result);
  }

  @Override
  public BookingDTO getBooking(Long userId, Long bookingId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new BookingNotFoundException(bookingId.toString()));

    Item item = itemRepository.findById(booking.getItem().getId())
        .orElseThrow(() -> new ItemNotFoundException(booking.getItem().getId().toString()));

    if (!userId.equals(item.getOwner().getId())
        && !userId.equals(booking.getBooker().getId())) {
      throw new UserNotFoundException(userId.toString());
    }
    return BookingMapper.toBookingDTO(booking);
  }

  @Override
  public List<BookingDTO> getBookingsByUser(Long bookerId, RequestBookingStatus state, Pageable pageable) {
    User user = userRepository.findById(bookerId)
        .orElseThrow(() -> new UserNotFoundException(bookerId.toString()));

    List<Booking> result = findBookingsByUserIdAndStatus(bookerId, state, pageable);
    return result.stream()
        .map(BookingMapper::toBookingDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<BookingDTO> getBookingStatusByOwner(Long ownerId, RequestBookingStatus state, Pageable pageable) {
    User user = userRepository.findById(ownerId)
        .orElseThrow(() -> new UserNotFoundException(ownerId.toString()));

    List<Item> items = itemRepository.findItemsByOwnerId(ownerId);
    if (items.isEmpty()) {
      throw new ItemNotFoundException(ownerId.toString());
    }
    return findBookingsByOwnerIdAndStatus(ownerId, state, pageable).stream()
        .map(BookingMapper::toBookingDTO)
        .collect(Collectors.toList());
  }

  private List<Booking> findBookingsByOwnerIdAndStatus(Long ownerId, RequestBookingStatus state, Pageable pageable) {
    switch (state) {
      case ALL:
        return bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(ownerId, pageable);
      case WAITING:
        return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
            BookingStatus.WAITING.name(), pageable);
      case REJECTED:
        return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
            BookingStatus.REJECTED.name(), pageable);
      case CURRENT:
        return bookingRepository.findCurrentBookingByOwnerId(ownerId, pageable);
      case PAST:
        return bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId,
            LocalDateTime.now(), pageable);
      case FUTURE:
        return bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId,
            LocalDateTime.now(), pageable);
      default:
        throw new RequestStatusException(state.name());
    }
  }

  private List<Booking> findBookingsByUserIdAndStatus(Long bookerId, RequestBookingStatus state, Pageable pageable) {
    switch (state) {
      case ALL:
        return bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId, pageable);
      case WAITING:
        return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
            BookingStatus.WAITING.name(), pageable);
      case REJECTED:
        return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
            BookingStatus.REJECTED.name(), pageable);
      case CURRENT:
        return bookingRepository.findCurrentBookingByBookerId(bookerId, pageable);
      case PAST:
        return bookingRepository.findPastBookingByBookerId(bookerId, pageable);
      case FUTURE:
        return bookingRepository.findFutureBookingByBookerId(bookerId, pageable);
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
