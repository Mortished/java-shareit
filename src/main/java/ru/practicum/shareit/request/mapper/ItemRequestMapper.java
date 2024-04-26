package ru.practicum.shareit.request.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

  public static ItemRequestDTO toItemRequestDTO(ItemRequest entity) {
    return ItemRequestDTO.builder()
        .id(entity.getId())
        .description(entity.getDescription())
        .created(entity.getCreated())
        .items(getItems(entity))
        .build();
  }

  public static ItemRequest toItemRequest(User user, RequestDTO requestDTO) {
    return ItemRequest.builder()
        .description(requestDTO.getDescription())
        .requestor(user)
        .build();
  }

  private static List<ItemDTO> getItems(ItemRequest entity) {
    if (entity.getItems() == null) {
      return Collections.emptyList();
    }
    if (entity.getItems().isEmpty()) {
      return Collections.emptyList();
    }
    return entity.getItems().stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

}
