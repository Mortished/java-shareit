package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {

  @NotNull
  private final Long itemId;

  @NotNull
  @FutureOrPresent
  private final LocalDateTime start;

  @NotNull
  @FutureOrPresent
  private final LocalDateTime end;

  private String status;

}
