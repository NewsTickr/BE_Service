package com.newstickr.newstickr.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newstickr.newstickr.news.dto.ReqPostNewsDto;
import com.newstickr.newstickr.news.dto.ResGetNewsDto;
import com.newstickr.newstickr.news.entity.News;
import com.newstickr.newstickr.news.repository.NewsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final String clientId = "ObFoodHkRv_jDEKKybn3";
    private final String clientSecret = "75g75IsXuf";
    private final String apiUrl = "https://openapi.naver.com/v1/search/news.json";

    private final NewsRepository newsRepository;



    @Override
    public JsonNode searchNews(String query) {

        RestTemplate restTemplate = new RestTemplate();

        // 요청 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("query", query)
                .queryParam("display", 8)
                .queryParam("sort", "sim")
                .toUriString();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        // HTTP 요청 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonNode;
    }

    @Override
    @Transactional
    public void createNewsPost(ReqPostNewsDto reqPostNewsDto) {
        News news = News.builder()
                .link(reqPostNewsDto.link())
                .title(reqPostNewsDto.title())
                .description(reqPostNewsDto.description())
                .analysis(reqPostNewsDto.analysis())
                .content(reqPostNewsDto.content())
                .created_at(LocalDateTime.now())
                .build();

        newsRepository.save(news);
    }

    @Override
    public List<ResGetNewsDto> searchNewsByTitle(String title) {
        List<News> newsList = newsRepository.findByTitleContainingIgnoreCase(title);
        return newsList.stream()
                .map(ResGetNewsDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteNewsPost(Long id) {
        if (!newsRepository.existsById(id)){
            throw new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다.");
        }
        newsRepository.deleteById(id);
    }
}
