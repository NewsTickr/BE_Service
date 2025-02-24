package com.newstickr.newstickr.comment.dto;

import com.newstickr.newstickr.news.dto.ResGetNewsDto;
import lombok.Data;

@Data
public class CommentResponseWithNews {
    ResGetNewsDto news;
    CommentResponse comment;
}
