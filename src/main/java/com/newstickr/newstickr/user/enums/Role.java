package com.newstickr.newstickr.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    public static Role fromString(String roleString) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getValue().equals(roleString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role value in token: " + roleString));
    }
}
