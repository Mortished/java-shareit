package ru.practicum.shareit.request;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

  private final ItemRequestService itemRequestService;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @PostMapping
  public ItemRequestDTO create(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @Valid @RequestBody final RequestDTO requestDTO) {
    return itemRequestService.create(userId, requestDTO);
  }

  @GetMapping
  public List<ItemRequestDTO> getSelfRequests(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId) {
    return itemRequestService.getSelfRequests(userId);
  }

  @GetMapping("/all")
  public List<ItemRequestDTO> getAllRequests(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @RequestParam(required = false, defaultValue = "0") final Integer from,
      @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return itemRequestService.getAll(userId, PageRequest.of(from, size, Sort.by("created").descending()));
  }

  @GetMapping("/{requestId}")
  public ItemRequestDTO getRequest(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @PathVariable("requestId") final Long requestId) {
    return itemRequestService.get(userId, requestId);
  }

}
