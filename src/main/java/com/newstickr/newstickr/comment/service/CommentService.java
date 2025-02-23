package com.newstickr.newstickr.comment.service;

import com.newstickr.newstickr.comment.dto.CommentRequest;
import com.newstickr.newstickr.comment.dto.CommentResponse;
import com.newstickr.newstickr.comment.entity.Comment;
import com.newstickr.newstickr.comment.repository.CommentRepository;
import com.newstickr.newstickr.news.entity.News;
import com.newstickr.newstickr.news.repository.NewsRepository;
import com.newstickr.newstickr.user.entity.User;
import com.newstickr.newstickr.user.repository.UserRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private UserRepository userRepository;

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
    // 특정 댓글 조회
//    public CommentResponse getComment(Long commentId) {
//        Optional<Comment> optional = commentRepository.findById(commentId);
//        if (optional.isPresent()) {
//            Comment comment = optional.get();
//            CommentResponse commentResponse = new CommentResponse();
//            commentResponse.setCommentId(commentId);
//            commentResponse.setContent(comment.getContent());
//            commentResponse.setLikeCount(comment.getLikeCount());
//            commentResponse.setCreatedAt(comment.getCreatedAt().toString());
//            commentResponse.setUserId(comment.getUser().getId());
//            commentResponse.setProfileImg(comment.getUser().getProfileImg());
//            commentResponse.setUsername(comment.getUser().getUsername());
//            return commentResponse;
//        }
//        throw new RuntimeException("No comment with id " + commentId); // 404 처리
//    }
    // 특정 사용자가 쓴 댓글 모두 조회
    public List<CommentResponse> getAllCommentsByUserId(Long userId) {
        List<Comment> commentList =  commentRepository.findByUser_Id(userId);
        return getCommentResponses(commentList);
    }
    // 특정 기사의 댓글 모두 조회
    public List<CommentResponse> getAllCommentsByNewsId(Long newsId) {
        List<Comment> commentList =  commentRepository.findByNews_NewsId(newsId);
        return getCommentResponses(commentList);
    }

    private List<CommentResponse> getCommentResponses(List<Comment> commentList) {
        List<CommentResponse> response = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setCommentId(comment.getCommentId());
            commentResponse.setContent(comment.getContent());
            commentResponse.setLikeCount(comment.getLikeCount());
            commentResponse.setCreatedAt(comment.getCreatedAt().toString());
            commentResponse.setUserId(comment.getUser().getId());
            commentResponse.setProfileImg(comment.getUser().getProfileImg());
            commentResponse.setUsername(comment.getUser().getUsername());

            response.add(commentResponse);
        }

        return response;
    }

    // 댓글 수정
    public boolean updateComment(Long commentId, CommentRequest commentRequest,Long userId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            Comment newComment = optional.get();
            if (newComment.getUser().getId().equals(userId)) {
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
            if (comment.getUser().getId().equals(userId)) {
                log.info("댓글 작성자와 jwt의 userId가 달라 거부");
                return false;
            }
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

}
