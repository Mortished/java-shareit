package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemFullDTO;

public interface ItemService {

  ItemDTO add(Long userId, ItemDTO item);

  ItemDTO edit(Long userId, Long itemId, ItemDTO item);

  ItemFullDTO getById(Long userId, Long id);

  List<ItemFullDTO> getUserItems(Long userId);

  List<ItemDTO> search(String text);

  CommentDTO comment(Long userId, Long itemId, CommentRequestDTO text);

}
