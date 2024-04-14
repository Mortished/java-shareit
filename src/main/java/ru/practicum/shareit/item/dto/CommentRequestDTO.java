package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentRequestDTO {

  @NotEmpty
  private String text;

}
