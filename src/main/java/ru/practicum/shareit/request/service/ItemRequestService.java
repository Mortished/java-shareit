package ru.practicum.shareit.request.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.RequestDTO;

public interface ItemRequestService {

  ItemRequestDTO create(Long userId, RequestDTO requestDTO);

  List<ItemRequestDTO> getSelfRequests(Long userId);

  List<ItemRequestDTO> getAll(Pageable pageable);

  ItemRequestDTO get(Long userId, Long id);

}
