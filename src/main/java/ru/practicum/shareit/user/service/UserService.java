package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDTO;

public interface UserService {

  UserDTO save(UserDTO user);

  UserDTO update(Long id, UserDTO user);

  List<UserDTO> getAll();

  UserDTO getById(Long id);

  void deleteById(Long id);


}
