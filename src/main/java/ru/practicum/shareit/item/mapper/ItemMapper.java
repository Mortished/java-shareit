package ru.practicum.shareit.item.mapper;

import java.util.List;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemBookingDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemFullDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

  public static ItemDTO toItemDto(Item item) {
    return ItemDTO.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
        .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
        .build();
  }

  public static ItemFullDTO toItemFullDTO(Item item, List<CommentDTO> comments, Booking lastBooking,
      Booking nextBooking) {
    return ItemFullDTO.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
        .lastBooking(lastBooking != null ? ItemBookingDTO.builder()
            .id(lastBooking.getId())
            .bookerId(lastBooking.getBooker().getId())
            .build() : null)
        .nextBooking(nextBooking != null ? ItemBookingDTO.builder()
            .id(nextBooking.getId())
            .bookerId(nextBooking.getBooker().getId())
            .build() : null)
        .comments(comments)
        .build();
  }

  public static Item toItem(ItemDTO dto, User owner) {
    return Item.builder()
        .id(dto.getId())
        .name(dto.getName())
        .description(dto.getDescription())
        .available(dto.getAvailable())
        .owner(owner)
        .build();
  }

  public static Item toItemWithRequest(ItemDTO dto, User owner, ItemRequest request) {
    return Item.builder()
        .id(dto.getId())
        .name(dto.getName())
        .description(dto.getDescription())
        .available(dto.getAvailable())
        .owner(owner)
        .request(request)
        .build();
  }

}
