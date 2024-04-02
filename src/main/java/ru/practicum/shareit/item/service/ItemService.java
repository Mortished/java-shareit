package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {

  ItemDto add(Long userId, ItemDto item);

  ItemDto edit(Long userId, Long itemId, ItemDto item);

  ItemDto getById(Long id);

  List<ItemDto> getUserItems(Long userId);

  List<ItemDto> search(String text);

}
