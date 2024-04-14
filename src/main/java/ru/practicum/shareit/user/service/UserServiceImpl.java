package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateUserEmailException;
import ru.practicum.shareit.exeption.UserNotFoundException;
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
    User result = null;
    try {
      result = userRepository.save(UserMapper.toUser(user));
    } catch (Exception e) {
      throw new DuplicateUserEmailException();
    }
    return UserMapper.toUserDto(result);
  }

  @Override
  public UserDTO update(Long id, UserDTO user) {
    Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isEmpty()) {
      throw new UserNotFoundException(id.toString());
    }
    User result = userOptional.get();
    if (user.getEmail() != null && user.getEmail().equals(result.getEmail())) {
      return getById(id);
    }
    user.setId(id);

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
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new UserNotFoundException(id.toString());
    }
    return UserMapper.toUserDto(user.get());
  }

  @Override
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

}
