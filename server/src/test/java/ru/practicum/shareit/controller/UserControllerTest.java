package ru.practicum.shareit.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
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
import ru.practicum.shareit.exeption.DuplicateUserEmailException;
import ru.practicum.shareit.exeption.ErrorHandlingControllerAdvice;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, ErrorHandlingControllerAdvice.class})
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @MockBean
  private UserService userService;

  private static final String URL = "http://localhost:8080/users";

  @Test
  void saveEmptyName() throws Exception {
    var user = User.builder()
        .email("a@a.ru")
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(user)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("name: must not be empty")));
  }

  @Test
  void saveNullEmail() throws Exception {
    var user = User.builder()
        .name("test")
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(user)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("email: must not be null")));
  }

  @Test
  void saveIncorrectEmail() throws Exception {
    var user = User.builder()
        .name("test")
        .email("test")
        .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(user)));

    response.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$[0].error", is("email: must be a well-formed email address")));
  }


  @Test
  void createUserDublicateEmail() throws Exception {
    when(userService.save(Mockito.any()))
        .thenThrow(DuplicateUserEmailException.class);

    var user = getDefault();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(user)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void updateUserNotFound() throws Exception {
    when(userService.update(Mockito.anyLong(), Mockito.any()))
        .thenThrow(UserNotFoundException.class);

    var user = getDefault();

    var response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{id}"), 1L)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(user)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  void saveUser() throws Exception {
    var user = getDefault();

    when(userService.save(Mockito.any()))
        .thenReturn(user);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .header("Content-Type", "application/json")
        .header("X-Sharer-User-Id", 1L)
        .content(mapper.writeValueAsString(user)));

    response.andExpect(status().isOk());
  }

  private UserDTO getDefault() {
    return UserDTO.builder()
        .name("test")
        .email("test@test.ru")
        .build();
  }

}
