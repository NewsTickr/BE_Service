package com.newstickr.newstickr.user.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileResponse {
    @NotBlank(message = "유저 닉네임")
    @Schema(description = "user nickname", example = "주린이")
    private String name;
    @NotBlank(message = "유저 이메일")
    @Schema(description = "user email", example = "example@email.com")
    private String email;
    @NotBlank(message = "유저 프로필 이미지 경로")
    @Schema(description = "user Img Url", example = "example.svg or example.png 등 등")
    private String profileImg;
}
