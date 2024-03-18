package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemDto {

  private Long id;
  private String name;
  private String description;
  private boolean available;

}
