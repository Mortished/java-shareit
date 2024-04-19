package ru.practicum.shareit.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseDTO {

  private Long id;
  private String name;

}
