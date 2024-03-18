package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateUserEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserDto save(UserDto user) {
    validateUser(user);
    userRepository.save(UserMapper.toUser(user));
    return user;
  }


  @Override
  public UserDto update(UserDto user) {
    return null;
  }

  @Override
  public List<UserDto> getAll() {
    return null;
  }

  @Override
  public UserDto getById(Long id) {
    return null;
  }

  @Override
  public void deleteById(Long id) {

  }

  private void validateUser(UserDto user) {
    if (isEmailExist(user)) {
      throw new DuplicateUserEmailException(user.getEmail());
    }
  }

  private boolean isEmailExist(UserDto user) {
    return userRepository.getAll().stream()
        .map(User::getEmail)
        .anyMatch(it -> it.equals(user.getEmail()));
  }

}
