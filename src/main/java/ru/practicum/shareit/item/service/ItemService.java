package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDTO;

public interface ItemService {

  ItemDTO add(Long userId, ItemDTO item);

  ItemDTO edit(Long userId, Long itemId, ItemDTO item);

  ItemDTO getById(Long id);

  List<ItemDTO> getUserItems(Long userId);

  List<ItemDTO> search(String text);

}
