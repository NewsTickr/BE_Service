package com.newstickr.newstickr.news.dto;

import com.newstickr.newstickr.news.entity.News;

public record ResGetNewsDto(
        Long id,
        String link,
        String title,
        String description,
        String analysis,
        String content,
        String userName,
        Long userId
) {
    public static ResGetNewsDto fromEntity(News news) {
        return new ResGetNewsDto(
                news.getNewsId(),
                news.getLink(),
                news.getTitle(),
                news.getDescription(),
                news.getAnalysis(),
                news.getContent(),
                news.getUser().getUsername(),
                news.getUser().getId()
        );
    }
}
