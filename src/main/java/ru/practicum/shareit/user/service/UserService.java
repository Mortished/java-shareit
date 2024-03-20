package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

  UserDto save(UserDto user);

  UserDto update(Long id, UserDto user);

  List<UserDto> getAll();

  UserDto getById(Long id);

  void deleteById(Long id);


}
