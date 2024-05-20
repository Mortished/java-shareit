package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.BaseDTO;

@Data
@Builder
public class BookingDTO {

  private Long id;
  private LocalDateTime start;
  private LocalDateTime end;
  private String status;
  private BaseDTO booker;
  private BaseDTO item;

}
