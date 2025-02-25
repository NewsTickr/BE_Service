package com.newstickr.newstickr.comment.service;

import com.newstickr.newstickr.comment.dto.CommentRequest;
import com.newstickr.newstickr.comment.dto.CommentResponse;
import com.newstickr.newstickr.comment.dto.CommentResponseWithNews;
import com.newstickr.newstickr.comment.entity.Comment;
import com.newstickr.newstickr.comment.entity.CommentLike;
import com.newstickr.newstickr.comment.repository.CommentLikeRepository;
import com.newstickr.newstickr.comment.repository.CommentRepository;
import com.newstickr.newstickr.news.dto.ResGetNewsDto;
import com.newstickr.newstickr.news.entity.News;
import com.newstickr.newstickr.news.repository.NewsRepository;
import com.newstickr.newstickr.user.entity.User;
import com.newstickr.newstickr.user.repository.UserRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Builder
    // 댓글 추가
    public void addComment(Long newsId, CommentRequest commentRequest, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        Optional<News> optionalNews = newsRepository.findById(newsId);
        if(optionalNews.isEmpty()) {
            throw new RuntimeException("News not found");
        }
        News news = optionalNews.get();
        try{
            Comment comment = new Comment();
            comment.setContent(URLEncoder.encode(commentRequest.getContent(), StandardCharsets.UTF_8));
            comment.setUser(user);
            comment.setNews(news);
            commentRepository.save(comment);
        }catch(Exception e){
            throw new RuntimeException(e);}
    }

    // 특정 사용자가 쓴 댓글 모두 조회
    public List<CommentResponseWithNews> getAllCommentsByUserId(Long userId) {
        List<Comment> commentList =  commentRepository.findByUser_Id(userId);
        return getCommentResponseWithNews(commentList);
    }
    // 특정 기사의 댓글 모두 조회
    public List<CommentResponse> getAllCommentsByNewsId(Long newsId) {
        List<Comment> commentList =  commentRepository.findByNews_NewsId(newsId);
        return getCommentResponses(commentList);
    }

    // 댓글 수정
    public boolean updateComment(Long commentId, CommentRequest commentRequest,Long userId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            Comment newComment = optional.get();
            if (newComment.getUser().getId() == null || !newComment.getUser().getId().equals(userId)) {
                log.info("댓글 작성자와 jwt의 userId가 달라 거부");
                return false;
            }
            newComment.setContent(commentRequest.getContent());
            commentRepository.save(newComment);
            return true;
        }
        return false;
    }
    // 댓글 삭제
    public boolean deleteComment(Long commentId, Long userId) {
        Optional<Comment> optional = commentRepository.findCommentByCommentId(commentId);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            if (comment.getUser().getId() == null||!comment.getUser().getId().equals(userId)) {
                log.info("댓글 작성자와 jwt의 userId가 달라 거부");
                return false;
            }
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

    // 좋아요 기능
    @Transactional
    public boolean likeComment(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<CommentLike> existingLike = commentLikeRepository.findByUserAndComment(user, comment);

        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 취소 (좋아요 삭제)
            commentLikeRepository.delete(existingLike.get());
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
            return false;  // 좋아요 취소됨
        } else {
            // 새로운 좋아요 추가
            CommentLike commentLike = new CommentLike(user, comment);
            commentLikeRepository.save(commentLike);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);
            return true;  // 좋아요 추가됨
        }
    }

    // 사용자가 댓글에 좋아요를 눌렀는지 확인
    public boolean isCommentLikedByUser(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return commentLikeRepository.findByUserAndComment(user, comment).isPresent();
    }

    // 사용자가 좋아요한 댓글들 조회
    public List<CommentResponseWithNews> getLikedCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CommentLike> likedComments = commentLikeRepository.findByUser(user);

        List<Comment> comments = likedComments.stream()
                .map(CommentLike::getComment)
                .collect(Collectors.toList());

        return getCommentResponseWithNews(comments);
    }

    private List<CommentResponse> getCommentResponses(List<Comment> comments) {
        List<CommentResponse> response = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setCommentId(comment.getCommentId());
            commentResponse.setContent(comment.getContent());
            commentResponse.setLikeCount(comment.getLikeCount());
            commentResponse.setCreatedAt(comment.getCreatedAt().toString());
            commentResponse.setUserId(comment.getUser().getId());
            commentResponse.setProfileImg(comment.getUser().getProfileImg());
            commentResponse.setUsername(comment.getUser().getName());

            response.add(commentResponse);
        }
        return response;
    }

    private List<CommentResponseWithNews> getCommentResponseWithNews(List<Comment> comments) {
        List<CommentResponseWithNews> response = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponseWithNews commentResponseWithNews = new CommentResponseWithNews();
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setCommentId(comment.getCommentId());
            commentResponse.setContent(comment.getContent());
            commentResponse.setLikeCount(comment.getLikeCount());
            commentResponse.setCreatedAt(comment.getCreatedAt().toString());
            commentResponse.setUserId(comment.getUser().getId());
            commentResponse.setProfileImg(comment.getUser().getProfileImg());
            commentResponse.setUsername(comment.getUser().getName());
            // news, comment 응답 저장
            commentResponseWithNews.setComment(commentResponse);
            commentResponseWithNews.setNews(ResGetNewsDto.fromEntity(comment.getNews()));

            response.add(commentResponseWithNews);
        }
        return response;
    }
}
