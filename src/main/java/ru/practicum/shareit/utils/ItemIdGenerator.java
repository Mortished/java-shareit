package ru.practicum.shareit.utils;

public class ItemIdGenerator {

  private static ItemIdGenerator instance;
  private Long id = 1L;

  private ItemIdGenerator() {
  }

  public static ItemIdGenerator getInstance() {
    if (instance == null) {
      instance = new ItemIdGenerator();
    }
    return instance;
  }

  public Long getId() {
    long result = id;
    id++;
    return result;
  }
}