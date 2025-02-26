package com.newstickr.newstickr.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Comment Create/Update request dto")
public class CommentRequest {
    @NotBlank(message = "댓글 내용")
    @Schema(description = "comment content", example = "영웅호걸들의 시간이다")
    private String content;
}
