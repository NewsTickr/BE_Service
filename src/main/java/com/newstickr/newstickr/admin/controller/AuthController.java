package com.newstickr.newstickr.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "로그인 및 로그아웃 관련 API")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Operation(summary = "로그아웃", description = "JWT 토큰을 담고 있는 Authorization 쿠키를 무효화 +  SecurityContext를 초기화하여 로그아웃을 수행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // JWT 삭제를 위해 쿠키 무효화
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);

        // SecurityContext 초기화 (세션 로그아웃)
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logged out successfully");
    }
}

