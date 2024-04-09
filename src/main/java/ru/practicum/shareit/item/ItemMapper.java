package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

  public static ItemDTO toItemDto(Item item) {
    return ItemDTO.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
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
