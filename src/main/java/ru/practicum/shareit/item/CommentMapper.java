package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toEntity(CommentDto dto) {
        return Comment.builder()
                .text(dto.text())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(
                        comment.getUser().getName()
                )
                .created(comment.getCreated())
                .build();
    }

    public static void patchFields(Comment comment, CommentDto dto) {
        if (Objects.nonNull(dto.text()) && !dto.text().isBlank())
            comment.setText(dto.text());
    }
}
