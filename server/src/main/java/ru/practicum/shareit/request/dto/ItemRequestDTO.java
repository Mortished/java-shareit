package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDTO;

@Data
@Builder
public class ItemRequestDTO {

  @NotNull
  private Long id;
  @NotEmpty
  private String description;
  @NotNull
  private LocalDateTime created;
  private List<ItemDTO> items;

}
