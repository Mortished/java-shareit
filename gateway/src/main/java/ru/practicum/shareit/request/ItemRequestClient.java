package ru.practicum.shareit.request;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDTO;

@Service
public class ItemRequestClient extends BaseClient {

  private static final String API_PREFIX = "/requests";

  @Autowired
  public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
      RestTemplateBuilder builder) {
    super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
  }

  public ResponseEntity<Object> create(Long userId, RequestDTO requestDTO) {
    return post("", userId, requestDTO);
  }

  public ResponseEntity<Object> getSelfRequests(Long userId) {
    return get("", userId);
  }

  public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
    Map<String, Object> param = Map.of("from", from, "size", size);
    return get("/all?from={from}&size={size}", userId, param);
  }

  public ResponseEntity<Object> get(Long userId, Long requestId) {
    return get("/" + requestId, userId);
  }
}
