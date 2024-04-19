package ru.practicum.shareit.exeption;

public class BookingNotFoundException extends RuntimeException {

  public BookingNotFoundException(String message) {
    super("Booking c id = " + message + " не найден!");
  }
}
