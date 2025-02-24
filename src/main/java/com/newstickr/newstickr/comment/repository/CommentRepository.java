package com.newstickr.newstickr.comment.repository;

import com.newstickr.newstickr.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findCommentByCommentId(Long commentId);
    List<Comment> findByUser_Id(Long userId);
    List<Comment> findByNews_NewsId(Long newsId);

    // 댓글 삭제 전에 연결된 좋아요 삭제하는 쿼리 추가
    @Modifying
    @Transactional
    @Query("DELETE FROM CommentLike cl WHERE cl.comment.commentId = :commentId")
    void deleteLikesByCommentId(@Param("commentId") Long commentId);
}
