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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserService userService;

  @GetMapping
  public List<UserDto> getAll() {
    return userService.getAll();
  }

  @GetMapping("/{id}")
  public UserDto getById(@PathVariable Long id) {
    return userService.getById(id);
  }

  @PostMapping
  public UserDto save(@Valid @RequestBody UserDto user) {
    return userService.save(user);
  }

  @PatchMapping("/{id}")
  public UserDto update(@PathVariable Long id, @RequestBody UserDto user) {
    return userService.update(user);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }

}
