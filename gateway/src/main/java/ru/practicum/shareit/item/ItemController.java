package ru.practicum.shareit.item;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemClient itemClient;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @PostMapping
  public ResponseEntity<Object> add(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @Valid @RequestBody ItemDTO item) {
    return itemClient.add(userId, item);
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<Object> edit(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @PathVariable Long itemId,
      @RequestBody ItemDTO item) {
    return itemClient.edit(userId, itemId, item);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<Object> getById(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @PathVariable Long itemId) {
    return itemClient.getById(userId, itemId);
  }

  @GetMapping
  public ResponseEntity<Object> getUserItems(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @RequestParam(required = false, defaultValue = "0") final Integer from,
      @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return itemClient.getUserItems(userId, from, size);
  }

  @GetMapping("/search")
  public ResponseEntity<Object> search(
      @RequestParam String text,
      @RequestParam(required = false, defaultValue = "0") final Integer from,
      @RequestParam(required = false, defaultValue = "10") final Integer size
  ) {
    return itemClient.search(text, from, size);
  }

  @PostMapping("/{itemId}/comment")
  public ResponseEntity<Object> comment(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @PathVariable Long itemId,
      @RequestBody @Valid CommentRequestDTO text) {
    return itemClient.comment(userId, itemId, text);
  }

}
