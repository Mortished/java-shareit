package ru.practicum.shareit.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ErrorHandlingControllerAdvice;
import ru.practicum.shareit.exeption.ItemNotAvalibleException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = {BookingController.class, ErrorHandlingControllerAdvice.class})
public class BookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @MockBean
  private BookingService bookingService;

  private static final String URL = "http://localhost:8080/bookings";

  @Test
  void bookNullItemId() throws Exception {

    var booking = BookingRequestDTO.builder()
        .start(LocalDateTime.now().plusDays(1))
        .end(LocalDateTime.now().plusDays(2))
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("itemId: must not be null")));
  }

  @Test
  void bookNullStart() throws Exception {

    var booking = BookingRequestDTO.builder()
        .itemId(1L)
        .end(LocalDateTime.now().plusDays(1))
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("start: must not be null")));
  }

  @Test
  void bookNullEnd() throws Exception {

    var booking = BookingRequestDTO.builder()
        .itemId(1L)
        .start(LocalDateTime.now().plusDays(1))
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("end: must not be null")));
  }

  @Test
  void bookPastStart() throws Exception {

    var booking = BookingRequestDTO.builder()
        .itemId(1L)
        .start(LocalDateTime.now().minusDays(1))
        .end(LocalDateTime.now().plusDays(1))
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath("$[0].error", is("start: must be a date in the present or in the future")));
  }

  @Test
  void bookPastEnd() throws Exception {

    var booking = BookingRequestDTO.builder()
        .itemId(1L)
        .start(LocalDateTime.now().plusDays(1))
        .end(LocalDateTime.now().minusDays(1))
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath("$[0].error", is("end: must be a date in the present or in the future")));
  }

  @Test
  void bookUserNotFound() throws Exception {
    Mockito.when(bookingService.book(Mockito.anyLong(), Mockito.any()))
        .thenThrow(UserNotFoundException.class);

    var booking = getDefault();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void updateBookingNotFound() throws Exception {
    Mockito.when(
            bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
        .thenThrow(BookingNotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{bookingId}"), 1L)
        .param("approved", Boolean.FALSE.toString())
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void getBookingItemNotFound() throws Exception {
    Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
        .thenThrow(ItemNotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{bookingId}"), 1L)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void bookItemNotAvalible() throws Exception {
    Mockito.when(bookingService.book(Mockito.anyLong(), Mockito.any()))
        .thenThrow(ItemNotAvalibleException.class);

    var booking = getDefault();
    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  private BookingRequestDTO getDefault() {
    return BookingRequestDTO.builder()
        .itemId(1L)
        .start(LocalDateTime.now().plusDays(1))
        .end(LocalDateTime.now().plusDays(2))
        .build();
  }

}
