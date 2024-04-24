package ru.practicum.shareit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
public class ItemRequestServiceTest {

  @Autowired
  private ItemRequestService itemRequestService;

  @MockBean
  private ItemRequestRepository itemRequestRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ItemRepository itemRepository;

  @Test
  public void createUserNotFound() {
    RequestDTO request = getDefaultRequestDTO();

    when(userRepository.findById(anyLong()))
        .thenThrow(UserNotFoundException.class);

    assertThrows(UserNotFoundException.class, () -> itemRequestService.create(1L, request));
    verify(userRepository, times(1)).findById(any());
  }

  @Test
  public void create() {
    ItemRequest itemRequest = getDefaultItemRequest();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRequestRepository.save(any()))
        .thenReturn(itemRequest);

    var result = itemRequestService.create(1L, getDefaultRequestDTO());
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemRequest.getId());
    assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).save(any());
  }

  @Test
  public void getByIdItemNotFound() {
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRequestRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.get(1L, 1L));

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
  }

  @Test
  public void getById() {
    ItemRequest itemRequest = getDefaultItemRequest();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRequestRepository.findById(anyLong()))
        .thenReturn(Optional.of(itemRequest));
    when(itemRepository.findAllByRequest_Id(anyLong()))
        .thenReturn(Collections.emptyList());

    var result = itemRequestService.get(1L, 1L);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemRequest.getId());
    assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());
    assertThat(result.getItems()).isEmpty();

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
  }

  @Test
  public void getAll() {
    ItemRequest itemRequest = getDefaultItemRequest();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRequestRepository.findById(anyLong()))
        .thenReturn(Optional.of(itemRequest));
    when(itemRequestRepository.findAllWithoutRequestorId(anyLong(), any()))
        .thenReturn(List.of(itemRequest));
    when(itemRepository.findAllByRequest_Id(anyLong()))
        .thenReturn(Collections.emptyList());

    var result = itemRequestService.getAll(1L, PageRequest.ofSize(1));
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
    assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
    assertThat(result.get(0).getItems()).isEmpty();

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findAllWithoutRequestorId(anyLong(), any());
    verify(itemRepository, times(1)).findAllByRequest_Id(any());
  }


  @Test
  public void getAllSelfRequests() {
    ItemRequest itemRequest = getDefaultItemRequest();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(getDefaultUser()));
    when(itemRequestRepository.findById(anyLong()))
        .thenReturn(Optional.of(itemRequest));
    when(itemRequestRepository.findAllByRequestorId(anyLong()))
        .thenReturn(List.of(itemRequest));
    when(itemRepository.findAllByRequest_Id(anyLong()))
        .thenReturn(Collections.emptyList());

    var result = itemRequestService.getSelfRequests(1L);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
    assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
    assertThat(result.get(0).getItems()).isEmpty();

    verify(userRepository, times(2)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
    verify(itemRepository, times(1)).findAllByRequest_Id(any());
  }

  private RequestDTO getDefaultRequestDTO() {
    return RequestDTO.builder()
        .description("test")
        .build();
  }

  private User getDefaultUser() {
    return User.builder()
        .id(1L)
        .name("name")
        .email("test@test.ru")
        .build();
  }

  private ItemRequest getDefaultItemRequest() {
    return ItemRequest.builder()
        .id(1L)
        .description("test")
        .requestor(getDefaultUser())
        .build();
  }

}
