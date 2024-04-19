package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

  public static CommentDTO toCommentDTO(Comment comment) {
    return CommentDTO.builder()
        .id(comment.getId())
        .text(comment.getText())
        .authorName(comment.getUser().getName())
        .created(comment.getCreated())
        .build();
  }

}
