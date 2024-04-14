package ru.practicum.shareit.exeption;

public class ValidateException extends RuntimeException {

  public ValidateException() {
    super("Сущность не прошла валидацию!");
  }

}
