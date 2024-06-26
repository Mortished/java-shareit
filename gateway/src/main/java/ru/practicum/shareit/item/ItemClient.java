package ru.practicum.shareit.item;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

@Service
public class ItemClient extends BaseClient {

  private static final String API_PREFIX = "/items";

  @Autowired
  public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
    super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
  }

  public ResponseEntity<Object> add(long userId, ItemDTO item) {
    return post("", userId, item);
  }

  public ResponseEntity<Object> edit(long userId, Long itemId, ItemDTO item) {
    return patch("/" + itemId, userId, item);
  }

  public ResponseEntity<Object> getById(long userId, Long itemId) {
    return get("/" + itemId, userId);
  }

  public ResponseEntity<Object> getUserItems(long userId, Integer from, Integer size) {
    Map<String, Object> params = Map.of("from", from, "size", size);
    return get("?from={from}&size={size}", userId, params);
  }


  public ResponseEntity<Object> search(String text, Integer from, Integer size) {
    Map<String, Object> params = Map.of("text", text, "from", from, "size", size);
    return get("/search?text={text}&from={from}&size={size}", null, params);
  }

  public ResponseEntity<Object> comment(long userId, Long itemId, CommentRequestDTO text) {
    return post("/" + itemId + "/comment", userId, text);
  }

}
