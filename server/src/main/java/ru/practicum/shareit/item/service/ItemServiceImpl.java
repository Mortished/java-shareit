package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidateException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemFullDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final BookingRepository bookingRepository;
  private final ItemRequestRepository requestRepository;

  @Override
  public ItemDTO add(Long userId, ItemDTO item) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    if (item.getRequestId() != null) {
      ItemRequest itemRequest = requestRepository.findById(item.getRequestId())
          .orElseThrow(() -> new ItemRequestNotFoundException(item.getRequestId().toString()));
      return ItemMapper.toItemDto(
          itemRepository.save(ItemMapper.toItemWithRequest(item, user, itemRequest)));
    }

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
  public ItemFullDTO getById(Long userId, Long id) {
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> new ItemNotFoundException(id.toString()));

    List<Booking> bookingList = bookingRepository.findBookingsByItemId(item.getId());
    Booking lastBooking = null;
    Booking nextBooking = null;
    if (bookingList.size() == 1) {
      lastBooking = getNextBooking(bookingList);
    } else if (bookingList.stream().map(Booking::getBooker)
        .map(User::getId)
        .noneMatch(it -> it.equals(userId))
    ) {
      lastBooking = getLastBooking(bookingList);
      nextBooking = getNextBooking(bookingList);

    }
    List<Comment> comments = commentRepository.findAllByItemId(item.getId());
    List<CommentDTO> commentsDTO = comments.stream()
        .map(CommentMapper::toCommentDTO)
        .collect(Collectors.toList());
    return ItemMapper.toItemFullDTO(item, commentsDTO, lastBooking, nextBooking);
  }

  @Override
  public List<ItemFullDTO> getUserItems(Long userId, Pageable pageable) {
    List<Item> items = itemRepository.findItemsByOwnerId(userId, pageable);
    return items.stream()
        .map(item -> getById(userId, item.getId()))
        .sorted(Comparator.comparing(ItemFullDTO::getId))
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDTO> search(String text, Pageable pageable) {
    if (text.isEmpty()) {
      return Collections.emptyList();
    }
    return itemRepository.search(text, pageable).stream()
        .distinct()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public CommentDTO comment(Long userId, Long itemId, CommentRequestDTO text) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));

    if (!bookingRepository.existsBookingByBookerIdAndStatus(userId,
        BookingStatus.APPROVED.name())) {
      throw new ValidateException();
    }
    Comment comment = commentRepository.save(Comment.builder()
        .text(text.getText())
        .item(item)
        .user(user)
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
        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
        .max(Comparator.comparing(Booking::getStart))
        .orElse(null);
  }

}
