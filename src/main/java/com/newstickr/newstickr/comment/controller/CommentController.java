package com.newstickr.newstickr.comment.controller;


import com.newstickr.newstickr.comment.dto.CommentRequest;
import com.newstickr.newstickr.comment.dto.CommentResponse;
import com.newstickr.newstickr.comment.dto.CommentResponseWithNews;
import com.newstickr.newstickr.comment.service.CommentService;
import com.newstickr.newstickr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Tag(name = "Comment API", description = "댓글 관련 API")
@CrossOrigin
@RequiredArgsConstructor
public class CommentController {
    // 의존성들 주입
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "User 댓글 조회", description = "사용자가 작성한 댓글을 리스트형태로 조회합니다.")
    public ResponseEntity<List<CommentResponseWithNews>> getUserComment(@PathVariable Long userId) {
        List<CommentResponseWithNews> response = commentService.getAllCommentsByUserId(userId);
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
    @GetMapping("/news/{newsId}")
    @Operation(summary = "뉴스에 작성된 댓글 조회")
    public ResponseEntity<List<CommentResponse>> getNewsComment(@PathVariable String newsId) {
        List<CommentResponse> response = commentService.getAllCommentsByNewsId(Long.parseLong(newsId));
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
    // 사용하지 않아도 될듯함.
//    @GetMapping("/{commentId}")
//    @Operation(summary = "")
//    public ResponseEntity<CommentResponse> getComment(@PathVariable String commentId) {
//        try{
//           CommentResponse response = commentService.getComment(Long.parseLong(commentId));
//            return ResponseEntity.status(HttpStatus.FOUND).body(response);
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }

    @PostMapping("/{newsId}")
    @Operation(summary = "뉴스에 댓글 추가")
    public ResponseEntity<Object> addComment(@PathVariable String newsId, @RequestBody CommentRequest commentRequest) {
        Long userId = userService.getUserIdByAuthentication();
        try {
            commentService.addComment(Long.parseLong(newsId), commentRequest, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/")
    @Operation(summary = "댓글 수정", description = "댓글식별자를 사용하여 댓글 내용 수정, jwt로 권한 확인")
    public ResponseEntity<Object> updateComment(@RequestParam("commentId") Long commentId, @RequestBody CommentRequest commentRequest) {

        Long userId = userService.getUserIdByAuthentication();
        boolean result = commentService.updateComment(commentId,commentRequest,userId);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/")
    @Operation(summary = "댓글 삭제", description = "댓글식별자를 사용하여 댓글 삭제. jwt로 권한 확인")
    public ResponseEntity<Object> deleteComment(@RequestParam("commentId") Long commentId) {
        Long userId = userService.getUserIdByAuthentication();
        boolean result = commentService.deleteComment(commentId,userId);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "좋아요 기능", description = "좋아요 버튼을 누르면 좋아요 카운트 +1, 이미 좋아요한 버튼을 누르면 카운트 -1")
    public ResponseEntity<String> likeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        boolean liked = commentService.likeComment(commentId, userId);
        if (liked) {
            return ResponseEntity.ok("좋아요 성공!");
        } else {
            return ResponseEntity.ok("좋아요 취소됨.");
        }
    }

    @GetMapping("/{commentId}/liked")
    @Operation(summary = "좋아요 여부 확인", description = "회원이 이미 좋아요를 눌렀는지 확인")
    public ResponseEntity<Boolean> isCommentLiked(@PathVariable Long commentId, @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.isCommentLikedByUser(commentId, userId));
    }

    @GetMapping("/liked")
    @Operation(summary = "사용자가 죻아요한 댓글 조회", description = "회원 ID를 기준으로 좋아요 누른 모든 댓글들을 조회")
    public ResponseEntity<List<CommentResponseWithNews>> getLikedComments(@RequestParam Long userId) {
        return ResponseEntity.ok(commentService.getLikedCommentsByUser(userId));
    }

}
