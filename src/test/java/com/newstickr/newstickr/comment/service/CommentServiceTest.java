package com.newstickr.newstickr.comment.service;

import com.newstickr.newstickr.comment.dto.CommentRequest;
import com.newstickr.newstickr.comment.dto.CommentResponse;
import com.newstickr.newstickr.comment.entity.Comment;
import com.newstickr.newstickr.comment.entity.News;
import com.newstickr.newstickr.comment.entity.User;
import com.newstickr.newstickr.comment.repository.CommentRepository;
import com.newstickr.newstickr.comment.repository.NewsRepository;
import com.newstickr.newstickr.comment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private User user;
    private News news;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        news = new News();
        news.setNewsId(1L);
        user.setUserId("testUserId");
        comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser(user);
        comment.setNews(news);
        comment.setContent("Test content");
        comment.setCreatedAt(new Date());

    }

    @Test
    void addComment() {
        // Given
        String userId = "testUser";
        Long newsId = 1L;
        String commentContent = "Test content";

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent(commentContent);
        commentRequest.setUserId(userId);

        User user = new User();
        user.setUserId(userId);
        user.setUserNickname("Test User");
        user.setUserPhoto("path/to/photo");

        News news = new News();
        news.setNewsId(newsId);

        Comment expectedComment = new Comment();
        expectedComment.setContent(commentContent);
        expectedComment.setUser(user);
        expectedComment.setNews(news);
        expectedComment.setLikeCount(0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));  // Mock user
        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));  // Mock news
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);  // Mock save

        // When
        Comment savedComment = commentService.addComment(newsId, commentRequest);

        // Then
        assertNotNull(savedComment);
        assertEquals(expectedComment.getContent(), savedComment.getContent());
        assertEquals(expectedComment.getUser(), savedComment.getUser());
        assertEquals(expectedComment.getNews(), savedComment.getNews());
        assertEquals(0, savedComment.getLikeCount());

        verify(userRepository, times(1)).findByUserId(userId);  // Verify user fetch
        verify(newsRepository, times(1)).findById(newsId);  // Verify news fetch
        verify(commentRepository, times(1)).save(any(Comment.class));  // Verify save method called
    }

    @Test
    void getComment() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        CommentResponse foundComment = commentService.getComment(1L);

        // Assert
        assertNotNull(foundComment);
        assertEquals(1L, foundComment.getCommentId());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void getAllCommentsByUserId() {
        // Arrange
        when(commentRepository.findByUser_UserId(user.getUserId())).thenReturn(List.of(comment));

        // Act & Assert
        List<CommentResponse> foundComment = commentService.getAllCommentsByUserId(user.getUserId());

        assertNotNull(foundComment);
        verify(commentRepository, times(1)).findByUser_UserId(comment.getUser().getUserId());
    }

    @Test
    void getAllCommentsByNewsId() {
        when(commentRepository.findByNews_NewsId(news.getNewsId())).thenReturn(List.of(comment));

        List<CommentResponse> foundComment = commentService.getAllCommentsByNewsId(news.getNewsId());

        assertNotNull(foundComment);
        verify(commentRepository, times(1)).findByNews_NewsId(news.getNewsId());
    }

    @Test
    void updateComment() {
        // Given
        Long commentId = 1L;
        String newContent = "Updated content";

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent(newContent);

        User user = new User();
        user.setUserId("testUser");
        user.setUserNickname("Test User");
        user.setUserPhoto("path/to/photo");

        News news = new News();
        news.setNewsId(1L);

        Comment existingComment = new Comment();
        existingComment.setCommentId(commentId);
        existingComment.setContent("Original content");
        existingComment.setUser(user);
        existingComment.setNews(news);
        existingComment.setLikeCount(0);

        Comment updatedComment = new Comment();
        updatedComment.setCommentId(commentId);
        updatedComment.setContent(newContent);
        updatedComment.setUser(user);
        updatedComment.setNews(news);
        updatedComment.setLikeCount(0);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));  // Mock findById
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);  // Mock save

        // When
        boolean result = commentService.updateComment(commentId, commentRequest);

        // Then
        assertTrue(result);
        assertEquals(newContent, existingComment.getContent());
        assertEquals(updatedComment.getContent(), existingComment.getContent());
        verify(commentRepository, times(1)).findById(commentId);  // Verify findById was called
        verify(commentRepository, times(1)).save(existingComment);  // Verify save method was called
    }


    @Test
    void testDeleteComment_success() {
        // given: findCommentByCommentId가 Optional.of(comment)를 반환하도록 설정
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));

        // when: deleteComment 호출
        boolean result = commentService.deleteComment(1L);

        // then: 결과가 true여야 하고, delete가 호출되어야 함
        assertTrue(result);
        verify(commentRepository, times(1)).delete(comment);  // commentRepository.delete()가 1번 호출되었는지 확인
    }

    @Test
    void testDeleteComment_notFound() {
        // given: findCommentByCommentId가 빈 Optional을 반환하도록 설정
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.empty());

        // when: deleteComment 호출
        boolean result = commentService.deleteComment(1L);

        // then: 결과가 false여야 하고, delete가 호출되지 않아야 함
        assertFalse(result);
        verify(commentRepository, never()).delete(any(Comment.class));  // delete()가 호출되지 않도록 검증
    }
}