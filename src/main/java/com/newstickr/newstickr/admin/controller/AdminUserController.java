package com.newstickr.newstickr.admin.controller;

import com.newstickr.newstickr.admin.dto.AdminUserListResponseDto;
import com.newstickr.newstickr.admin.service.AdminUserService;

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

}

