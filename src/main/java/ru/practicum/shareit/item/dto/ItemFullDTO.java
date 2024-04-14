package ru.practicum.shareit.item.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.BaseDTO;

@Data
@Builder
public class ItemFullDTO {

  private Long id;
  @NotEmpty
  private String name;
  @NotEmpty
  private String description;
  @NotNull
  private Boolean available;
  private BaseDTO lastBooking;
  private BaseDTO nextBooking;
  private List<CommentDTO> comments;

}
