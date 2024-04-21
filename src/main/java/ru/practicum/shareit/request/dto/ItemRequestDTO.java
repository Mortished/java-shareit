package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDTO;

@Data
@Builder
public class ItemRequestDTO {

  private Long id;
  private String description;
  private LocalDateTime created;
  private List<ItemDTO> items;

}
