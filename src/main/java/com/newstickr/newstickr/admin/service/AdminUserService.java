package com.newstickr.newstickr.admin.service;

import com.newstickr.newstickr.admin.dto.AdminUserListResponseDto;
import com.newstickr.newstickr.user.entity.User;
import com.newstickr.newstickr.admin.repository.AdminUserRepository;
import com.newstickr.newstickr.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

    // 1. 전체 회원 조회
    public Page<AdminUserListResponseDto> getAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users = adminUserRepository.findAll(pageRequest);
        return users.map(this::convertToDto);
    }

    // 회원 권한(Role) 변경
    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        Optional<User> optionalUser = adminUserRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(newRole); // Role 업데이트
            adminUserRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
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




