package ru.practicum.shareit.item.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ItemIdGenerator;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserRepository userRepository;

  @Override
  public ItemDto add(Long userId, ItemDto item) {
    Optional<User> result = userRepository.findById(userId);
    if (result.isEmpty()) {
      throw new UserNotFoundException(userId.toString());
    }
    User user = result.get();
    item.setId(ItemIdGenerator.getInstance().getId());
    itemRepository.save(ItemMapper.toItem(item, user));
    return item;
  }

  @Override
  public ItemDto edit(Long userId, Long itemId, ItemDto item) {
    Item result = itemRepository.getById(itemId);
    if (!Objects.equals(userId, result.getOwner().getId())) {
      throw new UserNotFoundException(userId.toString());
    }
    if (Objects.nonNull(item.getName())) {
      result.setName(item.getName());
    }
    if (Objects.nonNull(item.getDescription())) {
      result.setDescription(item.getDescription());
    }
    if (Objects.nonNull(item.getAvailable())) {
      result.setAvailable(item.getAvailable());
    }

    return ItemMapper.toItemDto(itemRepository.save(result));
  }

  @Override
  public ItemDto getById(Long id) {
    return ItemMapper.toItemDto(itemRepository.getById(id));
  }

  @Override
  public List<ItemDto> getUserItems(Long userId) {
    return itemRepository.findItemsByOwnerId(userId).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> search(String text) {
    if (text.isEmpty()) {
      return Collections.emptyList();
    }
    return itemRepository.searchAvailableItemsByNameOrDescriptionContainingIgnoreCase(text).stream()
        .distinct()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }
}
