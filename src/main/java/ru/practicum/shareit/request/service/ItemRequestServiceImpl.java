package ru.practicum.shareit.request.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

  private final ItemRequestRepository itemRequestRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  @Override
  public ItemRequestDTO create(Long userId, RequestDTO requestDTO) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    var result = itemRequestRepository.save(ItemRequestMapper.toItemRequest(user, requestDTO));
    return ItemRequestMapper.toItemRequestDTO(result);
  }

  @Override
  public List<ItemRequestDTO> getSelfRequests(Long userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    var result = itemRequestRepository.findAllByRequestorId(userId);
    return result.stream()
        .map(it -> get(userId, it.getId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemRequestDTO> getAll(Long userId, Pageable pageable) {
    List<ItemRequest> itemRequests = itemRequestRepository.findAllWithoutRequestorId(userId, pageable);
    return itemRequests.stream().
        map(it -> get(userId, it.getId()))
        .collect(Collectors.toList());
  }

  @Override
  public ItemRequestDTO get(Long userId, Long id) {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    ItemRequest request = itemRequestRepository.findById(id)
        .orElseThrow(() -> new ItemNotFoundException(id.toString()));
    List<ItemDTO> items = itemRepository.findAllByRequest_Id(request.getId()).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
    return ItemRequestMapper.toItemRequestDTO(request, items);
  }

}
