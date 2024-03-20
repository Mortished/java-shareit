package ru.practicum.shareit.item.dao;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {

  Item add(Item item);

  Item edit(Long itemId, Item item);

  Item getById(Long id);

  List<Item> getUserItems(Long userId);

  List<Item> search(String text);

}
