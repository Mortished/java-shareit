package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateUserEmailException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserDTO save(UserDTO user) {
    validateUser(user);
    return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
  }

  @Override
  public UserDTO update(Long id, UserDTO user) {
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

    return UserMapper.toUserDto(userRepository.save(result));
  }

  @Override
  public List<UserDTO> getAll() {
    return userRepository.findAll().stream()
        .map(UserMapper::toUserDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserDTO getById(Long id) {
    return UserMapper.toUserDto(userRepository.getById(id));
  }

  @Override
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  private void validateUser(UserDTO user) {
    if (isEmailExist(user.getEmail())) {
      throw new DuplicateUserEmailException(user.getEmail());
    }
  }

  private boolean isEmailExist(String email) {
    return userRepository.existsUserByEmail(email);
  }

}
