package ru.practicum.shareit.request.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
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
        .map(ItemRequestMapper::toItemRequestDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemRequestDTO> getAll(Long userId, Pageable pageable) {
    List<ItemRequest> itemRequests = itemRequestRepository.findAllWithoutRequestorId(userId,
        pageable);
    return itemRequests.stream()
        .map(ItemRequestMapper::toItemRequestDTO)
        .collect(Collectors.toList());
  }

  @Override
  public ItemRequestDTO get(Long userId, Long id) {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    ItemRequest request = itemRequestRepository.findById(id)
        .orElseThrow(() -> new ItemRequestNotFoundException(id.toString()));
    return ItemRequestMapper.toItemRequestDTO(request);
  }

}
