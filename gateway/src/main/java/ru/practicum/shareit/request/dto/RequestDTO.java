package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class RequestDTO {

  @NotEmpty
  private String description;

}
