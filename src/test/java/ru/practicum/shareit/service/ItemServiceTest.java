package ru.practicum.shareit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidateException;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
public class ItemServiceTest {

  @Autowired
  private ItemService itemService;

  @MockBean
  private ItemRepository itemRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private CommentRepository commentRepository;

  @MockBean
  private BookingRepository bookingRepository;

  @MockBean
  private ItemRequestRepository itemRequestRepository;

  @Test
  void addUserExeption() {
    ItemDTO itemDTO = getDefaultItemDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> itemService.add(1L, itemDTO));
    verify(userRepository, times(1)).findById(any());
  }

  @Test
  void addWithRequestId() {
    ItemDTO itemDTO = getDefaultItemDTO();
    itemDTO.setRequestId(1L);
    User user = getDefaultUser();
    Item item = getDefaultItem();
    ItemRequest itemRequest = getDefaultItemRequest();
    item.setRequest(itemRequest);

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(user));
    when(itemRequestRepository.findById(anyLong()))
        .thenReturn(Optional.of(itemRequest));
    when(itemRepository.save(any()))
        .thenReturn(item);

    var result = itemService.add(1L, itemDTO);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());
    assertThat(result.getRequestId()).isEqualTo(item.getRequest().getId());

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void addWithoutRequestId() {
    ItemDTO itemDTO = getDefaultItemDTO();
    User user = getDefaultUser();
    Item item = getDefaultItem();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(user));
    when(itemRepository.save(any()))
        .thenReturn(item);

    var result = itemService.add(1L, itemDTO);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());

    verify(userRepository, times(1)).findById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void editUserExeption() {
    ItemDTO itemDTO = getDefaultItemDTO();

    when(itemRepository.getById(anyLong()))
        .thenReturn(getDefaultItem());

    assertThrows(UserNotFoundException.class, () -> itemService.edit(2L, 1L, itemDTO));
    verify(itemRepository, times(1)).getById(any());
  }

  @Test
  void edit() {
    ItemDTO itemDTO = getDefaultItemDTO();

    when(itemRepository.getById(anyLong()))
        .thenReturn(getDefaultItem());
    when(itemRepository.save(any()))
        .thenReturn(getDefaultItem());

    var result = itemService.edit(1L, 1L, itemDTO);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemDTO.getId());
    assertThat(result.getName()).isEqualTo(itemDTO.getName());
    assertThat(result.getDescription()).isEqualTo(itemDTO.getDescription());
    assertThat(result.getAvailable()).isEqualTo(itemDTO.getAvailable());

    verify(itemRepository, times(1)).getById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void getByIdNotFoundException() {
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> itemService.getById(1L, 1L));
    verify(itemRepository, times(1)).findById(anyLong());

  }

  @Test
  void getById() {
    ItemDTO itemDTO = getDefaultItemDTO();
    Booking booking = getDefaultBooking();
    Comment comment = getDefaultComment();

    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItemId(anyLong()))
        .thenReturn(List.of(booking));
    when(commentRepository.findAllByItemId(anyLong()))
        .thenReturn(List.of(comment));

    var result = itemService.getById(1L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemDTO.getId());
    assertThat(result.getName()).isEqualTo(itemDTO.getName());
    assertThat(result.getDescription()).isEqualTo(itemDTO.getDescription());
    assertThat(result.getAvailable()).isEqualTo(itemDTO.getAvailable());
    assertThat(result.getNextBooking()).isNull();
    assertThat(result.getLastBooking()).isNotNull();
    assertThat(result.getComments()).hasSize(1);
    assertThat(result.getComments().get(0).getId()).isEqualTo(comment.getId());
    assertThat(result.getComments().get(0).getText()).isEqualTo(comment.getText());
    assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(comment.getUser().getName());

    verify(itemRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
    verify(commentRepository, times(1)).findAllByItemId(anyLong());

  }

  @Test
  void getByIdAllBookingDate() {
    ItemDTO itemDTO = getDefaultItemDTO();
    Booking booking1 = getDefaultBooking();
    booking1.setStart(LocalDateTime.now().minusDays(2));
    booking1.setEnd(LocalDateTime.now().minusDays(1));
    Booking booking2 = getDefaultBooking();
    Comment comment = getDefaultComment();

    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItemId(anyLong()))
        .thenReturn(List.of(booking1, booking2));
    when(commentRepository.findAllByItemId(anyLong()))
        .thenReturn(List.of(comment));

    var result = itemService.getById(2L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemDTO.getId());
    assertThat(result.getName()).isEqualTo(itemDTO.getName());
    assertThat(result.getDescription()).isEqualTo(itemDTO.getDescription());
    assertThat(result.getAvailable()).isEqualTo(itemDTO.getAvailable());
    assertThat(result.getNextBooking()).isNotNull();
    assertThat(result.getLastBooking()).isNotNull();
    assertThat(result.getComments()).hasSize(1);
    assertThat(result.getComments().get(0).getId()).isEqualTo(comment.getId());
    assertThat(result.getComments().get(0).getText()).isEqualTo(comment.getText());
    assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(comment.getUser().getName());

    verify(itemRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
    verify(commentRepository, times(1)).findAllByItemId(anyLong());

  }

  @Test
  void getUserItems() {
    ItemDTO itemDTO = getDefaultItemDTO();
    Booking booking = getDefaultBooking();
    Comment comment = getDefaultComment();

    when(itemRepository.findItemsByOwnerId(anyLong(), any()))
        .thenReturn(List.of(getDefaultItem()));
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(bookingRepository.findBookingsByItemId(anyLong()))
        .thenReturn(List.of(booking));
    when(commentRepository.findAllByItemId(anyLong()))
        .thenReturn(List.of(comment));

    var result = itemService.getUserItems(2L, null);
    var resultItemDTO = result.get(0);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(resultItemDTO.getId()).isEqualTo(itemDTO.getId());
    assertThat(resultItemDTO.getName()).isEqualTo(itemDTO.getName());
    assertThat(resultItemDTO.getDescription()).isEqualTo(itemDTO.getDescription());
    assertThat(resultItemDTO.getAvailable()).isEqualTo(itemDTO.getAvailable());
    assertThat(resultItemDTO.getNextBooking()).isNull();
    assertThat(resultItemDTO.getLastBooking()).isNotNull();
    assertThat(resultItemDTO.getComments()).hasSize(1);
    assertThat(resultItemDTO.getComments().get(0).getId()).isEqualTo(comment.getId());
    assertThat(resultItemDTO.getComments().get(0).getText()).isEqualTo(comment.getText());
    assertThat(resultItemDTO.getComments().get(0).getAuthorName()).isEqualTo(
        comment.getUser().getName());

    verify(itemRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findItemsByOwnerId(anyLong(), any());
    verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
    verify(commentRepository, times(1)).findAllByItemId(anyLong());

  }

  @Test
  void searchEmptyText() {
    var result = itemService.search("", null);
    assertThat(result).isEmpty();
  }

  @Test
  void search() {
    Item item = getDefaultItem();
    when(itemRepository.search(any(), any()))
        .thenReturn(List.of(item));

    var result = itemService.search("test", null);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo(item.getName());
    assertThat(result.get(0).getDescription()).isEqualTo(item.getDescription());
    assertThat(result.get(0).getAvailable()).isEqualTo(item.isAvailable());

    verify(itemRepository, times(1)).search(any(), any());
  }

  @Test
  void commentBookingExeption() {
    when(bookingRepository.existsBookingByBookerIdAndStatus(anyLong(), any()))
        .thenReturn(Boolean.FALSE);
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));

    assertThrows(ValidateException.class,
        () -> itemService.comment(1L, 1L, CommentRequestDTO.builder()
            .text("test")
            .build()));
  }

  @Test
  void comment() {
    when(bookingRepository.existsBookingByBookerIdAndStatus(anyLong(), any()))
        .thenReturn(Boolean.TRUE);
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultItem()));
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(commentRepository.save(any()))
        .thenReturn(getDefaultComment());

    var result = itemService.comment(1L, 1L, CommentRequestDTO.builder()
        .text("test")
        .build());

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getText()).isEqualTo("test");
    assertThat(result.getAuthorName()).isEqualTo("name");

    verify(itemRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).findById(anyLong());
    verify(commentRepository, times(1)).save(any());
    verify(bookingRepository, times(1)).existsBookingByBookerIdAndStatus(anyLong(), any());
  }


  private Comment getDefaultComment() {
    return Comment.builder()
        .id(1L)
        .text("test")
        .item(getDefaultItem())
        .user(getDefaultUser())
        .build();
  }

  private Booking getDefaultBooking() {
    return Booking.builder()
        .id(1L)
        .start(LocalDateTime.now().plusDays(1))
        .end(LocalDateTime.now().plusDays(2))
        .item(getDefaultItem())
        .booker(getDefaultUser())
        .status(BookingStatus.APPROVED.name())
        .build();
  }

  private ItemDTO getDefaultItemDTO() {
    return ItemDTO.builder()
        .id(1L)
        .name("test")
        .description("test")
        .available(Boolean.TRUE)
        .build();
  }

  private User getDefaultUser() {
    return User.builder()
        .id(1L)
        .name("name")
        .email("test@test.ru")
        .build();
  }

  private ItemRequest getDefaultItemRequest() {
    return ItemRequest.builder()
        .id(1L)
        .description("test")
        .requestor(getDefaultUser())
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

}
