package ru.practicum.shareit.user.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public class UserInMemoryDao implements UserRepository {

  private final Map<Long, User> data = new HashMap<>();

  @Override
  public User save(User user) {
    return data.put(user.getId(), user);
  }

  @Override
  public User update(Long id, User user) {
    return data.put(id, user);
  }

  @Override
  public List<User> getAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public User getById(Long id) {
    return data.get(id);
  }

  @Override
  public void deleteById(Long id) {
    data.remove(id);
  }

}
