package com.newstickr.newstickr.news.dto;

public record ReqPostNewsDto(
        String link,
        String title,
        String description,
        String analysis,
        String content
) {}
