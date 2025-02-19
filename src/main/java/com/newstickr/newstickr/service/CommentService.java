package com.newstickr.newstickr.service;

import com.newstickr.newstickr.dto.CommentRequest;
import com.newstickr.newstickr.dto.CommentResponse;
import com.newstickr.newstickr.entity.Comment;
import com.newstickr.newstickr.entity.News;
import com.newstickr.newstickr.entity.User;
import com.newstickr.newstickr.repository.CommentRepository;
import com.newstickr.newstickr.repository.NewsRepository;
import com.newstickr.newstickr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private UserRepository userRepository;

    // 댓글 추가
    public Comment addComment(Long newsId, CommentRequest commentRequest) {
        Optional<User> optionalUser = userRepository.findByUserId(commentRequest.getUserId());
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
            return commentRepository.save(comment);
        }catch(Exception e){
            throw new RuntimeException(e);}
    }
    // 특정 댓글 조회
    public CommentResponse getComment(Long commentId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setCommentId(commentId);
            commentResponse.setContent(comment.getContent());
            commentResponse.setLikeCount(comment.getLikeCount());
            commentResponse.setCreatedAt(comment.getCreatedAt().toString());
            commentResponse.setUserId(comment.getUser().getUserId());
            commentResponse.setUserPhoto(comment.getUser().getUserPhoto());
            commentResponse.setUserName(comment.getUser().getUserNickname());
            return commentResponse;
        }
        throw new RuntimeException("No comment with id " + commentId); // 404 처리
    }
    // 특정 사용자가 쓴 댓글 모두 조회
    public List<CommentResponse> getAllCommentsByUserId(String userId) {
        List<Comment> commentList =  commentRepository.findByUser_UserId(userId);
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
            commentResponse.setUserId(comment.getUser().getUserId());
            commentResponse.setUserPhoto(comment.getUser().getUserPhoto());
            commentResponse.setUserName(comment.getUser().getUserNickname());

            response.add(commentResponse);
        }

        return response;
    }

    // 댓글 수정
    public boolean updateComment(Long commentId, CommentRequest commentRequest) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            Comment newComment = optional.get();
            newComment.setContent(commentRequest.getContent());
            commentRepository.save(newComment);
            return true;
        }
        return false;
    }
    // 댓글 삭제
    public boolean deleteComment(Long commentId) {
        Optional<Comment> optional = commentRepository.findCommentByCommentId(commentId);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

}
