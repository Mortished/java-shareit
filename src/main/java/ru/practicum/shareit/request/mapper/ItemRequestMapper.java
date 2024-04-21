package ru.practicum.shareit.request.mapper;

import java.time.LocalDateTime;
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
        .build();
  }

  public static ItemRequest toItemRequest(User user, RequestDTO requestDTO) {
    return ItemRequest.builder()
        .description(requestDTO.getDescription())
        .requestor(user)
        .build();
  }

}
