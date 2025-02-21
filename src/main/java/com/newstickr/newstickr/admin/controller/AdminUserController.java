package com.newstickr.newstickr.admin.controller;

import com.newstickr.newstickr.admin.dto.AdminUserListResponseDto;
import com.newstickr.newstickr.admin.service.AdminUserService;
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
}

