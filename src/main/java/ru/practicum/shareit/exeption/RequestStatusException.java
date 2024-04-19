package ru.practicum.shareit.exeption;

public class RequestStatusException extends RuntimeException {

  public RequestStatusException(String message) {
    super("Unknown state: " + message);
  }

}
