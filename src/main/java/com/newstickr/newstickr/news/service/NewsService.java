package com.newstickr.newstickr.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.newstickr.newstickr.news.dto.ReqPostNewsDto;
import com.newstickr.newstickr.news.dto.ResGetNewsDto;

import java.util.List;

public interface NewsService {

    JsonNode searchNews(String query);

    void createNewsPost(ReqPostNewsDto reqPostNewsDto);

    List<ResGetNewsDto> searchNewsByTitle(String title);

    void deleteNewsPost(Long id);
}
