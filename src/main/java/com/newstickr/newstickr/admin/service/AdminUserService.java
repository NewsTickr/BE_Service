package com.newstickr.newstickr.admin.service;

import com.newstickr.newstickr.admin.dto.AdminUserListResponseDto;
import com.newstickr.newstickr.user.entity.User;
import com.newstickr.newstickr.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminuserRepository;

    // 1. 전체 회원 조회
    public Page<AdminUserListResponseDto> getAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users = adminuserRepository.findAll(pageRequest);
        return users.map(this::convertToDto);
    }

    // username 제외
    private AdminUserListResponseDto convertToDto(User user) {
        return new AdminUserListResponseDto(
                user.getId(),
                user.getName(),
                user.getProfileImg(),
                user.getEmail(),
                user.getRole()
        );
    }
    static class CustomException extends RuntimeException {
        public CustomException(String message) {
            super(message);
        }
    }
}




