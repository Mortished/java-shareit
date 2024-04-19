package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemBookingDTO {

  private Long id;
  private Long bookerId;

}
