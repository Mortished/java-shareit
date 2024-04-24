package ru.practicum.shareit.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exeption.ErrorHandlingControllerAdvice;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = {ItemController.class, ErrorHandlingControllerAdvice.class})
public class ItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @MockBean
  private ItemService itemService;

  private static final String URL = "http://localhost:8080/items";

  @Test
  void addEmptyName() throws Exception {
    ItemDTO item = ItemDTO.builder()
        .id(1L)
        .description("test")
        .available(Boolean.FALSE)
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("name: must not be empty")));

  }

  @Test
  void addEmptyDescription() throws Exception {
    ItemDTO item = ItemDTO.builder()
        .id(1L)
        .name("test")
        .available(Boolean.TRUE)
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("description: must not be empty")));
  }

  @Test
  void addNullAvalible() throws Exception {
    ItemDTO item = ItemDTO.builder()
        .id(1L)
        .name("test")
        .description("test")
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("available: must not be null")));
  }

  @Test
  void addUserNotFound() throws Exception {
    var item = getDefaultItem();
    when(itemService.add(anyLong(), any()))
        .thenThrow(UserNotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void getByIdNotFound() throws Exception {
    var item = getDefaultItem();
    when(itemService.getById(anyLong(), anyLong()))
        .thenThrow(ItemNotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{itemId}"), 1L)
        .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void add() throws Exception {
    var item = getDefaultItem();
    when(itemService.add(anyLong(), any()))
        .thenReturn(item);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("X-Sharer-User-Id", 1L)
        .header("Content-Type", "application/json")
        .content(mapper.writeValueAsString(item)));

    response.andExpect(status().isOk());
  }

  private ItemDTO getDefaultItem() {
    return ItemDTO.builder()
        .id(1L)
        .name("test")
        .description("test")
        .available(Boolean.TRUE)
        .build();
  }

}
