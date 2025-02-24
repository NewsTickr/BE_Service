package com.newstickr.newstickr.admin.controller;

import com.newstickr.newstickr.admin.dto.AdminUserListResponseDto;
import com.newstickr.newstickr.admin.service.AdminUserService;
import com.newstickr.newstickr.comment.service.CommentService;
import com.newstickr.newstickr.news.service.NewsService;
import com.newstickr.newstickr.user.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User API", description = "관리자 회원 관리 API")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final CommentService commentService;
    private final NewsService newsService;


    // 1. 전체 회원 조회 API
    @GetMapping("/info")
    @Operation(summary = "전체 회원 조회", description = "페이지네이션을 적용하여 전체 회원 목록을 조회합니다.")
    public ResponseEntity<Page<AdminUserListResponseDto>> getMembers(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam int size) {
        return ResponseEntity.ok(adminUserService.getAllUsers(page, size));
    }

    // 회원 권한(Role) 변경 API 추가
    @PatchMapping("/{id}/role")
    @Operation(summary = "회원 권한 변경", description = "회원의 역할(Role)을 변경합니다.")
    public ResponseEntity<?> updateUserRole(
            @Parameter(description = "회원 ID", example = "2") @PathVariable Long id,
            @Parameter(description = "변경할 역할 (예: ROLE_USER, ROLE_ADMIN)", example = "ROLE_ADMIN") @RequestParam String role) {
        try {
            Role newRole = Role.fromString(role); // 문자열을 Role Enum으로 변환
            adminUserService.updateUserRole(id, newRole);
            return ResponseEntity.ok("User role updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role value");
        }
    }

    // 3. 댓글 삭제 API
    @DeleteMapping("/{userId}/comments/{commentId}")
    @Operation(summary = "회원 댓글 삭제", description = "관리자가 특정 사용자의 댓글을 삭제합니다.")
    public ResponseEntity<?> deleteUserComment(
            @Parameter(description = "회원 ID", example = "2") @PathVariable Long userId,
            @Parameter(description = "댓글 ID", example = "10") @PathVariable Long commentId) {
        boolean deleted = commentService.deleteComment(commentId, userId);
        if (deleted) {
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete comment. Either comment does not exist or user mismatch.");
        }
    }

    // 4. 뉴스 게시글 삭제 API
    @DeleteMapping("/news/{newsId}")
    @Operation(summary = "뉴스 게시글 삭제", description = "관리자가 특정 뉴스 게시글을 삭제합니다.")
    public ResponseEntity<?> deleteNewsPost(
            @Parameter(description = "뉴스 게시글 ID", example = "5") @PathVariable Long newsId) {
        try {
            newsService.deleteNewsPost(newsId);
            return ResponseEntity.ok("News post deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to delete news post. It may not exist.");
        }
    }
}

