package com.newstickr.newstickr.comment.controller;


import com.newstickr.newstickr.comment.dto.CommentRequest;
import com.newstickr.newstickr.comment.dto.CommentResponse;
import com.newstickr.newstickr.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Tag(name = "Comment API", description = "댓글 추가, 조회, 수정, 삭제를 위한 API입니다.")
@CrossOrigin
public class CommentController {
    // 의존성들 주입
    @Autowired
    private CommentService commentService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "User 댓글 조회", description = "사용자가 작성한 댓글을 리스트형태로 조회합니다.")
    public ResponseEntity<List<CommentResponse>> getUserComment(@PathVariable String userId) {
        List<com.newstickr.newstickr.comment.dto.CommentResponse> response = commentService.getAllCommentsByUserId(userId);
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<com.newstickr.newstickr.comment.dto.CommentResponse>> getNewsComment(@PathVariable String newsId) {
        List<com.newstickr.newstickr.comment.dto.CommentResponse> response = commentService.getAllCommentsByNewsId(Long.parseLong(newsId));
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
    @GetMapping("/{commentId}")
    public ResponseEntity<com.newstickr.newstickr.comment.dto.CommentResponse> getComment(@PathVariable String commentId) {
        try{
           com.newstickr.newstickr.comment.dto.CommentResponse response = commentService.getComment(Long.parseLong(commentId));
            return ResponseEntity.status(HttpStatus.FOUND).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{newsId}")
    public ResponseEntity<Object> addComment(@PathVariable String newsId, @RequestBody CommentRequest commentRequest) {
        try {
            commentService.addComment(Long.parseLong(newsId), commentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/")
    public ResponseEntity<Object> updateComment(@RequestParam("commentId") Long commentId, @RequestBody com.newstickr.newstickr.comment.dto.CommentRequest commentRequest) {
        boolean result = commentService.updateComment(commentId,commentRequest);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/")
    public ResponseEntity<Object> deleteComment(@RequestParam("commentId") Long commentId) {
        boolean result = commentService.deleteComment(commentId);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
