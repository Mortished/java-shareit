package ru.practicum.shareit.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exeption.ErrorHandlingControllerAdvice;
import ru.practicum.shareit.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = {ItemRequestController.class, ErrorHandlingControllerAdvice.class})
public class ItemRequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @MockBean
  private ItemRequestService itemRequestService;

  private static final String URL = "http://localhost:8080/requests";

  @Test
  void createEmptyDescription() throws Exception {

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(ItemRequestDTO.builder().build())));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("description: must not be empty")));
  }


  @Test
  void createUserNotFound() throws Exception {
    Mockito.when(itemRequestService.create(Mockito.anyLong(), Mockito.any()))
        .thenThrow(UserNotFoundException.class);

    var itemRequestDTO = getDefault();
    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(itemRequestDTO)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void getByIdRequestNotFound() throws Exception {
    Mockito.when(itemRequestService.get(Mockito.anyLong(), Mockito.anyLong()))
        .thenThrow(ItemRequestNotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{requestId}"), 1L)
        .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  private ItemRequestDTO getDefault() {
    return ItemRequestDTO.builder()
        .description("test")
        .build();
  }


}
