package ru.practicum.shareit.item;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;
  private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

  @PostMapping
  public ItemDto add(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @Valid @RequestBody ItemDto item) {
    return itemService.add(userId, item);
  }

  @PatchMapping("/{itemId}")
  public ItemDto edit(
      @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
      @PathVariable Long itemId,
      @RequestBody ItemDto item) {
    return itemService.edit(userId, itemId, item);
  }

  @GetMapping("/{itemId}")
  public ItemDto getById(@PathVariable Long itemId) {
    return itemService.getById(itemId);
  }

  @GetMapping
  public List<ItemDto> getUserItems(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
    return itemService.getUserItems(userId);
  }

  @GetMapping("/search")
  public List<ItemDto> search(@RequestParam String text) {
    return itemService.search(text);
  }

}
