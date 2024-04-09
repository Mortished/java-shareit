package ru.practicum.shareit.user;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserService userService;

  @GetMapping
  public List<UserDTO> getAll() {
    return userService.getAll();
  }

  @GetMapping("/{id}")
  public UserDTO getById(@PathVariable Long id) {
    return userService.getById(id);
  }

  @PostMapping
  public UserDTO save(@Valid @RequestBody UserDTO user) {
    return userService.save(user);
  }

  @PatchMapping("/{id}")
  public UserDTO update(@PathVariable Long id, @RequestBody UserDTO user) {
    return userService.update(id, user);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }

}
