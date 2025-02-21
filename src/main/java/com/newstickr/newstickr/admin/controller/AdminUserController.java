package com.newstickr.newstickr.admin.controller;

import com.newstickr.newstickr.admin.dto.AdminUserListResponseDto;
import com.newstickr.newstickr.admin.service.AdminUserService;

import com.newstickr.newstickr.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // 1. 전체 회원 조회 API
    @GetMapping("/info")
    public ResponseEntity<Page<AdminUserListResponseDto>> getMembers(
            @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(adminUserService.getAllUsers(page, size));
    }

    // 회원 권한(Role) 변경 API 추가
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            Role newRole = Role.fromString(role); // 문자열을 Role Enum으로 변환
            adminUserService.updateUserRole(id, newRole);
            return ResponseEntity.ok("User role updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role value");
        }
    }
}

