package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findItemsByOwnerId(Long userId);

  @Query("select i from Item i where i.available = true "
      + "and upper(i.description) like upper(concat('%', ?1, '%')) "
      + "or upper(i.name) like upper(concat('%', ?1, '%')) "
      + "group by i.id")
  List<Item> search(String text);

  @Query("update Item i set i.available = ?2 where i.id = ?1")
  void updateItemAvalibleById(Long id, boolean available);

}
