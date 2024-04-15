package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequestDTO {

  @NotEmpty
  private String text;

}
