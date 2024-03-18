package ru.practicum.shareit.item.model;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class Item {

  private Long id;
  @NotEmpty
  private String name;
  @NotEmpty
  private String description;
  private boolean available;
  private User owner;
  private ItemRequest request;

}
