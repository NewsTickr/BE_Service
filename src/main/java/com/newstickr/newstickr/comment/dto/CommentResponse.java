package com.newstickr.newstickr.comment.dto;

import lombok.Data;

@Data
public class CommentResponse {
    private long commentId;
    private String content;
    private int likeCount;
    private String createdAt; //작성일
    private Long userId; // 작성자 조회를 위한 id
    private String username; // 작성자
    private String profileImg; //이거는 멀티파트로 혹은 경로로 전달

}
