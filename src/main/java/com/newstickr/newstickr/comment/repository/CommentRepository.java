package com.newstickr.newstickr.comment.repository;

import com.newstickr.newstickr.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findCommentByCommentId(Long commentId);
    List<Comment> findByUser_UserId(String userId);
    List<Comment> findByNews_NewsId(Long newsId);
}
