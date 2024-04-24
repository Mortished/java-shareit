package ru.practicum.shareit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotAvalibleException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
public class BookingServiceTest {

  @Autowired
  private BookingService bookingService;

  @MockBean
  private BookingRepository bookingRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ItemRepository itemRepository;

  @MockBean
  private EntityManager entityManager;

  @Test
  void bookingValidateEqualsDate() {
    var date = LocalDateTime.now();
    BookingRequestDTO bookingDTO = BookingRequestDTO.builder()
        .itemId(1L)
        .start(date)
        .end(date)
        .build();

    assertThrows(ValidateException.class, () -> bookingService.book(1L, bookingDTO));
  }

  @Test
  void bookingValidateDate() {
    BookingRequestDTO bookingDTO = BookingRequestDTO.builder()
        .itemId(1L)
        .start(LocalDateTime.now().plusDays(1))
        .end(LocalDateTime.now())
        .build();

    assertThrows(ValidateException.class, () -> bookingService.book(1L, bookingDTO));
  }

  @Test
  void bookingUserNotFound() {
    BookingRequestDTO bookingRequestDTO = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> bookingService.book(1L, bookingRequestDTO));

    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void bookingItemNotFound() {
    BookingRequestDTO bookingRequestDTO = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class,
        () -> bookingService.book(1L, bookingRequestDTO));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void bookingSelfItemExeption() {
    BookingRequestDTO bookingRequestDTO = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));

    assertThrows(UserNotFoundException.class,
        () -> bookingService.book(1L, bookingRequestDTO));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void bookingItemNotAvalibleException() {
    BookingRequestDTO bookingRequestDTO = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(itemRepository.isItemAvalible(anyLong()))
        .thenReturn(Boolean.FALSE);

    assertThrows(ItemNotAvalibleException.class, () -> bookingService.book(2L, bookingRequestDTO));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).isItemAvalible(anyLong());
  }

  @Test
  void booking() {
    BookingRequestDTO bookingRequestDTO = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(itemRepository.isItemAvalible(anyLong()))
        .thenReturn(Boolean.TRUE);
    when(bookingRepository.save(any()))
        .thenReturn(getDefaultBooking());

    var result = bookingService.book(2L, bookingRequestDTO);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2L);
    assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
    assertThat(result.getStart()).isEqualTo(bookingRequestDTO.getStart());
    assertThat(result.getEnd()).isEqualTo(bookingRequestDTO.getEnd());
    assertThat(result.getItem()).isNotNull();
    assertThat(result.getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).isItemAvalible(anyLong());
    verify(bookingRepository, times(1)).save(any());
  }

  @Test
  void updateBookingUserNotFoundException() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingBookingNotFoundException() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(BookingNotFoundException.class,
        () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingStatusException() {
    Booking booking = getDefaultBooking();
    booking.setStatus(BookingStatus.APPROVED.name());

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(booking));

    assertThrows(ValidateException.class,
        () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingItemException() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class,
        () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingOwnerException() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));

    assertThrows(UserNotFoundException.class,
        () -> bookingService.updateBooking(2L, 1L, Boolean.TRUE));

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingApprove() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));

    var result = bookingService.updateBooking(1L, 1L, Boolean.TRUE);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2L);
    assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
    assertThat(result.getStart()).isEqualTo(booking.getStart());
    assertThat(result.getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.getItem()).isNotNull();
    assertThat(result.getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(2)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).updateItemAvailableById(anyLong(), anyBoolean());
    verify(bookingRepository, times(1)).updateBookingStatusById(anyLong(), any());
  }

  @Test
  void updateBookingDissmiss() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));

    var result = bookingService.updateBooking(1L, 1L, Boolean.FALSE);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2L);
    assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
    assertThat(result.getStart()).isEqualTo(booking.getStart());
    assertThat(result.getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.getItem()).isNotNull();
    assertThat(result.getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(2)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).updateBookingStatusById(anyLong(), any());
  }

  @Test
  void getBookingUserException() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingBookingNotFoundException() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingItemNotFoundException() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultBooking()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingUserNotFoundException() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultBooking()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));

    assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(2L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBooking() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findById(anyLong()))
        .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));

    var result = bookingService.getBooking(1L, 1L);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(booking.getId());
    assertThat(result.getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.getStart()).isEqualTo(booking.getStart());
    assertThat(result.getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.getItem()).isNotNull();
    assertThat(result.getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingsByUserExeption() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> bookingService.getBookingsByUser(1L, RequestBookingStatus.ALL, null));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingsByUserAll() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findBookingByBookerIdOrderByStartDesc(anyLong(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.ALL, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingByBookerIdOrderByStartDesc(anyLong(), any());
  }

  @Test
  void getBookingsByUserWaiting() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.WAITING, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
        any(), any());
  }

  @Test
  void getBookingsByUserRejected() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.REJECTED, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
        any(), any());
  }

  @Test
  void getBookingsByUserCurrent() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findCurrentBookingByBookerId(anyLong(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.CURRENT, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findCurrentBookingByBookerId(anyLong(), any());
  }

  @Test
  void getBookingsByUserPast() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findPastBookingByBookerId(anyLong(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.PAST, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findPastBookingByBookerId(anyLong(), any());
  }

  @Test
  void getBookingsByUserFuture() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(bookingRepository.findFutureBookingByBookerId(anyLong(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.FUTURE, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findFutureBookingByBookerId(anyLong(), any());
  }

  @Test
  void getBookingStatusByOwnerExeption() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL, null));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingStatusByOwnerItemExeption() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(Collections.emptyList());

    assertThrows(ItemNotFoundException.class,
        () -> bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL, null));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
  }


  @Test
  void getBookingStatusByOwnerAll() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(List.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(),
        any());
  }

  @Test
  void getBookingStatusByOwnerWaiting() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(List.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
        any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.WAITING, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(
        anyLong(), any(), any());
  }

  @Test
  void getBookingStatusByOwnerRejected() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(List.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
        any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.REJECTED, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(
        anyLong(), any(), any());
  }

  @Test
  void getBookingStatusByOwnerCurrent() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(List.of(getDefaultItem()));
    when(bookingRepository.findCurrentBookingByOwnerId(anyLong(), any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.CURRENT, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    verify(bookingRepository, times(1)).findCurrentBookingByOwnerId(anyLong(), any());
  }

  @Test
  void getBookingStatusByOwnerPast() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(List.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(),
        any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.PAST, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(
        anyLong(), any(), any());
  }

  @Test
  void getBookingStatusByOwnerFuture() {
    Booking booking = getDefaultBooking();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRepository.findItemsByOwnerId(anyLong()))
        .thenReturn(List.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(),
        any()))
        .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.FUTURE, null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(
        anyLong(), any(), any());
  }


  private BookingRequestDTO getBookingRequestDTO() {
    return BookingRequestDTO.builder()
        .itemId(1L)
        .start(LocalDateTime.parse("2030-01-01T00:00:00"))
        .end(LocalDateTime.parse("2030-02-01T00:00:00"))
        .build();
  }

  private User getDefaultUser() {
    return User.builder()
        .id(1L)
        .name("name")
        .email("test@test.ru")
        .build();
  }

  private Item getDefaultItem() {
    return Item.builder()
        .id(1L)
        .name("test")
        .description("test")
        .owner(getDefaultUser())
        .available(Boolean.TRUE)
        .build();
  }

  private Booking getDefaultBooking() {
    return Booking.builder()
        .id(2L)
        .start(LocalDateTime.parse("2030-01-01T00:00:00"))
        .end(LocalDateTime.parse("2030-02-01T00:00:00"))
        .item(getDefaultItem())
        .booker(getDefaultUser())
        .status(BookingStatus.WAITING.name())
        .build();
  }

}
