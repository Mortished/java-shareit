package ru.practicum.shareit.item.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public class ItemInMemoryDao implements ItemRepository {

  private final Map<Long, Item> itemsData = new HashMap<>();

  @Override
  public Item add(Item item) {
    return itemsData.put(item.getId(), item);
  }

  @Override
  public Item edit(Long itemId, Item item) {
    return itemsData.put(itemId, item);
  }

  @Override
  public Item getById(Long id) {
    return itemsData.get(id);
  }

  @Override
  public List<Item> getUserItems(Long userId) {
    return itemsData.values().stream()
        .filter(it -> it.getOwner().getId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Item> search(String text) {
    return itemsData.values().stream()
        .filter(Item::isAvailable)
        .filter(it -> it.getName().toLowerCase().contains(text.toLowerCase())
            || it.getDescription().toLowerCase().contains(text.toLowerCase()))
        .collect(Collectors.toList());
  }
}
