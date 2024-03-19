package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateUserEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.UserIdGenerator;

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
  public UserDto update(Long id, UserDto user) {
    User result = userRepository.getById(id);
    if (user.getEmail() != null && user.getEmail().equals(result.getEmail())) {
      return getById(id);
    }
    user.setId(id);
    validateUser(user);

    if (user.getName() != null) {
      result.setName(user.getName());
    }
    if (user.getEmail() != null) {
      result.setEmail(user.getEmail());
    }
    userRepository.update(id, result);
    return getById(id);
  }

  @Override
  public List<UserDto> getAll() {
    return userRepository.getAll().stream()
        .map(UserMapper::toUserDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserDto getById(Long id) {
    return UserMapper.toUserDto(userRepository.getById(id));
  }

  @Override
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  private void validateUser(UserDto user) {
    if (isEmailExist(user.getEmail())) {
      throw new DuplicateUserEmailException(user.getEmail());
    }
    if (user.getId() == null) {
      user.setId(UserIdGenerator.getInstance().getId());
    }
  }

  private boolean isEmailExist(String email) {
    return userRepository.getAll().stream()
        .map(User::getEmail)
        .anyMatch(it -> it.equals(email));
  }

}
