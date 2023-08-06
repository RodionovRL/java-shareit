package ru.practicum.shareit.item.comment.repositiry.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "SELECT * " +
            "FROM  comments c " +
            "WHERE c.item_id IN :itemIds ;", nativeQuery = true)
    List<Comment> findAllInItemId(@Param("itemIds") List<Long> itemIds);
}
