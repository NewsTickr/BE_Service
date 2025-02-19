package com.newstickr.newstickr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Comment Create/Update request dto")
public class CommentRequest {
    @NotBlank(message = "사용자의 ID, DB에서 PK의 역할을 함.")
    @Schema(description = "user id", example = "James")
    private String userId;

    @NotBlank(message = "댓글 내용")
    @Schema(description = "comment content", example = "영웅호걸들의 시간이다")
    private String content;
}
