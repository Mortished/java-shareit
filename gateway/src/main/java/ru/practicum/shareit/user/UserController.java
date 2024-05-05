package ru.practicum.shareit.user;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class UserController {

  private final UserClient userClient;

  @PostMapping
  public ResponseEntity<Object> save(@Valid @RequestBody UserDTO user) {
    return userClient.save(user);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDTO user) {
    return userClient.update(id, user);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getById(@PathVariable Long id) {
    return userClient.getById(id);
  }

  @GetMapping
  public ResponseEntity<Object> getAll() {
    return userClient.getAll();
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    userClient.deleteById(id);
  }

}
