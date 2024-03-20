package ru.practicum.shareit.utils;

public class UserIdGenerator {

  private static UserIdGenerator instance;
  private Long id = 1L;

  private UserIdGenerator() {
  }

  public static UserIdGenerator getInstance() {
    if (instance == null) {
      instance = new UserIdGenerator();
    }
    return instance;
  }

  public Long getId() {
    long result = id;
    id++;
    return result;
  }
}