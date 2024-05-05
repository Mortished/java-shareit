package ru.practicum.shareit.user;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

}
