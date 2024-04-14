package ru.practicum.shareit.exeption;

public class DuplicateUserEmailException extends RuntimeException {

  public DuplicateUserEmailException() {
    super("Email = уже существует!");
  }
}
