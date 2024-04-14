package ru.practicum.shareit.item.mapper;

import java.util.List;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemFullDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BaseDTO;

public class ItemMapper {

  public static ItemDTO toItemDto(Item item) {
    return ItemDTO.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
        .build();
  }

  public static ItemFullDTO toItemFullDTO(Item item, List<CommentDTO> comments, Booking lastBooking,
      Booking nextBooking) {
    return ItemFullDTO.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
        .lastBooking(lastBooking != null ? BaseDTO.builder()
            .id(lastBooking.getId())
            .name(lastBooking.getBooker().getName())
            .build() : null)
        .nextBooking(nextBooking != null ? BaseDTO.builder()
            .id(nextBooking.getId())
            .name(nextBooking.getBooker().getName())
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
}
