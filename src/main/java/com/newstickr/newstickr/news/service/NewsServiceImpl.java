package com.newstickr.newstickr.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newstickr.newstickr.news.dto.ReqPostNewsDto;
import com.newstickr.newstickr.news.dto.ResGetNewsDto;
import com.newstickr.newstickr.news.entity.News;
import com.newstickr.newstickr.news.repository.NewsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Repository
public class NewsServiceImpl implements NewsService {

    // 네이버 관련
    private final String clientId = "ObFoodHkRv_jDEKKybn3";
    private final String clientSecret = "75g75IsXuf";
    private final String naverApiUrl = "https://openapi.naver.com/v1/search/news.json";

    // Groq 관련
    private final String apiKey = "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";
    private static final String groqApiUrl = "https://api.groq.com/openai/v1/chat/completions";

    private final NewsRepository newsRepository;



    @Override
    public JsonNode searchNews(String query) {

        RestTemplate restTemplate = new RestTemplate();

        // 요청 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl(naverApiUrl)
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
        String analysis = analyzeSentiment(reqPostNewsDto.description());

        News news = News.builder()
                .link(reqPostNewsDto.link())
                .title(reqPostNewsDto.title())
                .description(reqPostNewsDto.description())
                .analysis(analysis)
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

    public String analyzeSentiment(String summary) {
        RestTemplate restTemplate = new RestTemplate();

        // Groq API 요청 JSON 생성
        Map<String, Object> requestBody = Map.of(
                "model", "llama3-8b-8192",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an AI that performs sentiment analysis on news summaries."),
                        Map.of("role", "user", "content", "기사 요약 : " + summary + "\n2~3줄로 이 기사에 대한 감정 분석을 한국어로 해줘.")
                )
        );
        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    groqApiUrl, HttpMethod.POST, requestEntity, String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            return responseJson.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Groq API 요청 실패 : " + e.getMessage(), e);
        }
    }
}
