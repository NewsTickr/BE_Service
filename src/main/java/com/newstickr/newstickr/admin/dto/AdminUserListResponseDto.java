package com.newstickr.newstickr.admin.dto;

import com.newstickr.newstickr.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminUserListResponseDto {
    private Long id;

    // private String username; // OAuth2에서 제공하는 식별자

    private String name;

    private String profileImg; // 프로필 이미지 경로

    private String email;

    private Role role;
}
