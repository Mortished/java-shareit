package ru.practicum.shareit.item.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.utils.ItemIdGenerator;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserRepository userRepository;

  @Override
  public ItemDto add(Long userId, ItemDto item) {
    User user = userRepository.getById(userId);
    if (user == null) {
      throw new UserNotFoundException(userId.toString());
    }
    item.setId(ItemIdGenerator.getInstance().getId());
    itemRepository.add(ItemMapper.toItem(item, user));
    return item;
  }

  @Override
  public ItemDto edit(Long userId, Long itemId, ItemDto item) {
    Item result = itemRepository.getById(itemId);
    if (!Objects.equals(userId, result.getOwner().getId())) {
      throw new UserNotFoundException(userId.toString());
    }
    if (item.getName() != null) {
      result.setName(item.getName());
    }
    if (item.getDescription() != null) {
      result.setDescription(item.getDescription());
    }
    if (item.getAvailable() != null) {
      result.setAvailable(item.getAvailable());
    }
    itemRepository.edit(itemId, result);
    return ItemMapper.toItemDto(result);
  }

  @Override
  public ItemDto getById(Long id) {
    return ItemMapper.toItemDto(itemRepository.getById(id));
  }

  @Override
  public List<ItemDto> getUserItems(Long userId) {
    return itemRepository.getUserItems(userId).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> search(String text) {
    if (text.isEmpty()) {
      return Collections.emptyList();
    }
    return itemRepository.search(text).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }
}
