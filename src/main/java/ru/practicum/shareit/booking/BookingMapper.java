package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BaseDTO;

public class BookingMapper {

  public static BookingDTO toBookingDTO(Booking booking) {
    return BookingDTO.builder()
        .id(booking.getId())
        .start(booking.getStart())
        .end(booking.getEnd())
        .status(booking.getStatus())
        .booker(BaseDTO.builder()
            .id(booking.getBooker().getId())
            .build())
        .item(BaseDTO.builder()
            .id(booking.getItem().getId())
            .name(booking.getItem().getName())
            .build())
        .build();
  }

  public static Booking toBooking(BookingDTO dto, User user, Item item) {
    return Booking.builder()
        .id(dto.getId())
        .start(dto.getStart())
        .end(dto.getEnd())
        .status(dto.getStatus())
        .item(item)
        .booker(user)
        .build();
  }

  public static Booking toBooking(BookingRequestDTO dto, User user, Item item) {
    return Booking.builder()
        .start(dto.getStart())
        .end(dto.getEnd())
        .status(dto.getStatus())
        .item(item)
        .booker(user)
        .build();
  }

}
