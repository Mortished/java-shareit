package ru.practicum.shareit.request;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

  @Query("select i from ItemRequest i where i.requestor.id != ?1 order by i.created DESC")
  List<ItemRequest> findAllWithoutRequestorId(Long userId);

}
