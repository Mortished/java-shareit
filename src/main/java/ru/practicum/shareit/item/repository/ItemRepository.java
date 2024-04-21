package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findItemsByOwnerId(Long userId);
  List<Item> findItemsByOwnerId(Long userId, Pageable pageable);

  @Query("select i.available from Item i where i.id = ?1")
  boolean isItemAvalible(Long id);

  @Query("select i from Item i where i.available = true "
      + "and upper(i.description) like upper(concat('%', ?1, '%')) "
      + "or upper(i.name) like upper(concat('%', ?1, '%')) "
      + "group by i.id")
  List<Item> search(String text, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("update Item i set i.available = :available where i.id = :id")
  void updateItemAvailableById(@Param("id") Long id, @Param("available") boolean available);

}
