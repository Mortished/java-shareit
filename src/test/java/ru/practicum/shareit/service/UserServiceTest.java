package ru.practicum.shareit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exeption.DuplicateUserEmailException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
public class UserServiceTest {

  @MockBean
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Test
  void createDublicateEmail() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.save(any()))
        .thenThrow(DuplicateUserEmailException.class);

    assertThrows(DuplicateUserEmailException.class, () -> userService.save(user));
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void save() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.save(any()))
        .thenReturn(UserMapper.toUser(user));
    var result = userService.save(user);

    assertThat(result).usingRecursiveComparison().isEqualTo(user);
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void patchNotFoundUser() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.findById(anyLong()))
        .thenThrow(UserNotFoundException.class);

    assertThrows(UserNotFoundException.class, () -> userService.update(1L, user));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void emailWithoutChanges() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(User.builder()
            .id(1L)
            .name("test")
            .email("test@test.com")
            .build()));

    var result = userService.update(1L, user);
    assertThat(result).usingRecursiveComparison().isEqualTo(user);
    verify(userRepository, times(2)).findById(anyLong());
  }

  @Test
  void patchAllFields() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(User.builder()
            .id(1L)
            .name("first")
            .email("first@test.com")
            .build()));
    when(userRepository.save(any()))
        .thenReturn(UserMapper.toUser(user));

    var result = userService.update(1L, user);
    assertThat(result).usingRecursiveComparison().isEqualTo(user);
    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void findAll() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.findAll())
        .thenReturn(List.of(UserMapper.toUser(user)));

    var result = userService.getAll();
    assertThat(result).usingRecursiveComparison().isEqualTo(List.of(user));
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void getById() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(UserMapper.toUser(user)));

    var result = userService.getById(1L);
    assertThat(result).usingRecursiveComparison().isEqualTo(user);
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getByIdNotFoundException() {
    UserDTO user = getDefaultUserDTO();

    when(userRepository.findById(anyLong()))
        .thenThrow(UserNotFoundException.class);

    assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void deleteById() {
    userService.deleteById(1L);
    verify(userRepository, times(1)).deleteById(anyLong());
  }

  private UserDTO getDefaultUserDTO() {
    return UserDTO.builder()
        .id(1L)
        .name("test")
        .email("test@test.com")
        .build();
  }
}
