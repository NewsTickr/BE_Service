package com.newstickr.newstickr.user.entity;

import com.newstickr.newstickr.user.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // OAuth2에서 제공하는 식별자

    private String name;

    private String profileImg; // 프로필 이미지 경로

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role; // Enum 문자열로 저장 (USER, ADMIN)

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}