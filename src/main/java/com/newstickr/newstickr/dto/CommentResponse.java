package com.newstickr.newstickr.dto;

import lombok.Data;

@Data
public class CommentResponse {
    private long commentId;
    private String content;
    private int likeCount;
    private String createdAt; //작성일
    private String userId; // 작성자 조회를 위한 id
    private String userName; // 작성자
    private String userPhoto; //이거는 멀티파트로 혹은 경로로 전달

}
