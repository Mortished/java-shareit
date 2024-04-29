package ru.practicum.shareit.exeption;

public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException(String message) {
    super("Item c id = " + message + " не найден!");
  }
}
