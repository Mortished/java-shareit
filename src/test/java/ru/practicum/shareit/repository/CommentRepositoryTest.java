package ru.practicum.shareit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CommentRepositoryTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Test
  void checkQuery() {
    final User user = User.builder()
        .id(1L)
        .name("test")
        .email("test@test.ru")
        .build();

    userRepository.save(user);

    final Item item = Item.builder()
        .id(1L)
        .name("test")
        .description("test")
        .available(Boolean.TRUE)
        .owner(user)
        .build();

    itemRepository.save(item);

    final Comment comment = Comment.builder()
        .text("test")
        .item(item)
        .user(user)
        .build();

    var result = commentRepository.findAllByItemId(commentRepository.save(comment).getId());

    assertThat(result)
        .isNotNull()
        .hasSize(1)
        .usingRecursiveComparison()
        .isEqualTo(List.of(comment));
  }

}
