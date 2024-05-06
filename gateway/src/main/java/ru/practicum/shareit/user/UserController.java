package ru.practicum.shareit.user;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDTO;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

  private final UserClient userClient;

  @GetMapping
  public ResponseEntity<Object> getAll() {
    log.info("GET ALL users");
    return userClient.getAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getById(@PathVariable Long id) {
    log.info("GET user by ID userId={}", id);
    return userClient.getById(id);
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody @Valid UserDTO user) {
    log.info("POST user user={}", user);
    return userClient.save(user);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDTO user) {
    log.info("Patch user {}, userId={}", user, id);
    return userClient.update(id, user);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(@PathVariable Long id) {
    log.info("DELETE user by ID userId={}", id);
    return userClient.deleteById(id);
  }

}
