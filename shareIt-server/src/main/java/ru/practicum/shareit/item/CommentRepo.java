package ru.practicum.shareit.item;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepo extends CrudRepository<Comment, Long> {
}
