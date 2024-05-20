package ru.practicum.shareit.request;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDTO;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

  private final ItemRequestClient itemRequestClient;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @PostMapping
  public ResponseEntity<Object> create(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @Valid @RequestBody final RequestDTO requestDTO) {
    return itemRequestClient.create(userId, requestDTO);
  }

  @GetMapping
  public ResponseEntity<Object> getSelfRequests(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId) {
    return itemRequestClient.getSelfRequests(userId);
  }

  @GetMapping("/all")
  public ResponseEntity<Object> getAllRequests(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @RequestParam(required = false, defaultValue = "0") final Integer from,
      @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return itemRequestClient.getAll(userId, from, size);
  }

  @GetMapping("/{requestId}")
  public ResponseEntity<Object> getRequest(
      @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
      @PathVariable("requestId") final Long requestId) {
    return itemRequestClient.get(userId, requestId);
  }

}
