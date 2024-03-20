package ru.practicum.shareit.exeption;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super("User c id = " + message + " не найден!");
  }
}
