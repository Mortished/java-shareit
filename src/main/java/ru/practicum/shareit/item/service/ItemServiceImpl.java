package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemFullDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final BookingRepository bookingRepository;

  @Override
  public ItemDTO add(Long userId, ItemDTO item) {
    Optional<User> result = userRepository.findById(userId);
    if (result.isEmpty()) {
      throw new UserNotFoundException(userId.toString());
    }
    User user = result.get();
    return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(item, user)));
  }

  @Override
  public ItemDTO edit(Long userId, Long itemId, ItemDTO item) {
    Item result = itemRepository.getById(itemId);
    if (!Objects.equals(userId, result.getOwner().getId())) {
      throw new UserNotFoundException(userId.toString());
    }
    if (Objects.nonNull(item.getName())) {
      result.setName(item.getName());
    }
    if (Objects.nonNull(item.getDescription())) {
      result.setDescription(item.getDescription());
    }
    if (Objects.nonNull(item.getAvailable())) {
      result.setAvailable(item.getAvailable());
    }

    return ItemMapper.toItemDto(itemRepository.save(result));
  }

  @Override
  public ItemFullDTO getById(Long id) {
    Optional<Item> item = itemRepository.findById(id);
    if (item.isEmpty()) {
      throw new ItemNotFoundException(id.toString());
    }
    List<Booking> bookingList = bookingRepository.findBookingsByItemId(item.get().getId());
    List<Comment> comments = commentRepository.findAllByItemId(item.get().getId());
    List<CommentDTO> commentsDTO = comments.stream()
        .map(CommentMapper::toCommentDTO)
        .collect(Collectors.toList());
    Booking lastBooking = getLastBooking(bookingList);
    Booking nextBooking = getNextBooking(bookingList);
    return ItemMapper.toItemFullDTO(item.get(), commentsDTO, lastBooking, nextBooking);
  }

  @Override
  public List<ItemFullDTO> getUserItems(Long userId) {
    List<Item> items = itemRepository.findItemsByOwnerId(userId);
    return items.stream()
        .map(item -> getById(item.getId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDTO> search(String text) {
    if (text.isEmpty()) {
      return Collections.emptyList();
    }
    return itemRepository.search(text).stream()
        .distinct()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public CommentDTO comment(Long userId, Long itemId, String text) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(userId.toString());
    }
    Optional<Item> item = itemRepository.findById(itemId);
    if (item.isEmpty()) {
      throw new ItemNotFoundException(itemId.toString());
    }
    if (!bookingRepository.existsBookingByBookerIdAndStatus(userId, BookingStatus.APPROVED.name())) {
      throw new BookingNotFoundException(userId.toString());
    }
    Comment comment = commentRepository.save(Comment.builder()
        .text(text)
        .item(item.get())
        .user(user.get())
        .build());
    return CommentMapper.toCommentDTO(comment);
  }

  private Booking getNextBooking(List<Booking> bookingList) {
    return bookingList.stream()
        .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED.name()))
        .sorted(Comparator.comparing(Booking::getEnd))
        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
        .findFirst()
        .orElse(null);
  }

  private Booking getLastBooking(List<Booking> bookingList) {
    return bookingList.stream()
        .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED.name()))
        .sorted((o1, o2) -> o1.getEnd().isAfter(o2.getEnd()) ? 1 : -1)
        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
        .findFirst()
        .orElse(null);
  }

}
