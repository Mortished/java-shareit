package ru.practicum.shareit.user.dao;

import java.util.List;
import ru.practicum.shareit.user.model.User;

public interface UserRepository {

  User save(User user);

  User update(Long id, User user);

  List<User> getAll();

  User getById(Long id);

  void deleteById(Long id);

}
