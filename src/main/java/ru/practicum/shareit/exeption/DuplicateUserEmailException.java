package ru.practicum.shareit.exeption;

public class DuplicateUserEmailException extends RuntimeException {

  public DuplicateUserEmailException(String message) {
    super("Email = " + message + " уже существует!");
  }
}
