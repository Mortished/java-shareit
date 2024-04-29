package ru.practicum.shareit.exeption;

public class ItemRequestNotFoundException extends RuntimeException {

  public ItemRequestNotFoundException(String message) {
    super("ItemRequest c id = " + message + " не найден!");
  }
}
