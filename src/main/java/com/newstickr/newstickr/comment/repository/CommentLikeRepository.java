package com.newstickr.newstickr.comment.repository;

import com.newstickr.newstickr.comment.entity.Comment;
import com.newstickr.newstickr.comment.entity.CommentLike;
import com.newstickr.newstickr.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
    List<CommentLike> findByUser(User user);
}
