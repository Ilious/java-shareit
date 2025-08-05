package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toEntity(CommentDto dto, User user, Item item) {
        return Comment.builder()
                .text(dto.text())
                .created(LocalDateTime.now())
                .user(user)
                .item(item)
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        if (Objects.isNull(comment))
            return null;

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(
                        comment.getUser().getName()
                )
                .created(comment.getCreated())
                .build();
    }
}
