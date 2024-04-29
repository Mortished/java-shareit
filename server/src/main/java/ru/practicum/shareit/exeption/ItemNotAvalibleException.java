package ru.practicum.shareit.exeption;

public class ItemNotAvalibleException extends RuntimeException {

  public ItemNotAvalibleException(String message) {
    super("Item c id = " + message + " не активна!");
  }
}
