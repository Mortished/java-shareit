package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDTO {

  private Long id;
  @NotEmpty
  private String name;
  @NotEmpty
  private String description;
  @NotNull
  private Boolean available;
  private Long requestId;

}
