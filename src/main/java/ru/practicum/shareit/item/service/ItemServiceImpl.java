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
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserRepository userRepository;

  @Override
  public ItemDTO add(Long userId, ItemDTO item) {
    Optional<User> result = userRepository.findById(userId);
    if (result.isEmpty()) {
      throw new UserNotFoundException(userId.toString());
    }
    User user = result.get();
    return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(item, user)));
  }

  @Override
  public ItemDTO edit(Long userId, Long itemId, ItemDTO item) {
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
  public ItemDTO getById(Long id) {
    return ItemMapper.toItemDto(itemRepository.getById(id));
  }

  @Override
  public List<ItemDTO> getUserItems(Long userId) {
    return itemRepository.findItemsByOwnerId(userId).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDTO> search(String text) {
    if (text.isEmpty()) {
      return Collections.emptyList();
    }
    return itemRepository.searchAvailableItemsByNameOrDescriptionContainingIgnoreCase(text).stream()
        .distinct()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }
}
