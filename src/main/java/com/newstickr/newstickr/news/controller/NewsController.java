package com.newstickr.newstickr.news.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.newstickr.newstickr.news.dto.ReqPostNewsDto;
import com.newstickr.newstickr.news.dto.ResGetNewsDto;
import com.newstickr.newstickr.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "News API", description = "증권 뉴스 게시글 관련 API")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    @Operation(summary = "관련 뉴스 가져오기", description = "키워드로 관심 있는 종목을 검색해 관련 뉴스 기사를 가져옵니다.")
    public JsonNode getNews(@RequestParam String query) {
        return newsService.searchNews(query);
    }

    @PostMapping("/post")
    @Operation(summary = "관련 뉴스 게시글 작성하기", description = "소통하고 싶은 주제의 뉴스를 선택해 소통할 수 있는 게시글을 작성합니다.")
    public ResponseEntity<String> createNewsPost(@RequestBody ReqPostNewsDto reqPostNewsDto) {
        newsService.createNewsPost(reqPostNewsDto);
        return ResponseEntity.ok("News Post Created Successfully!!");
    }

    @GetMapping("/{title}")
    @Operation(summary = "게시글 검색하기", description = "키워드로 작성된 게시글을 검색합니다.")
    public ResponseEntity<List<ResGetNewsDto>> getNewsByTitle(@PathVariable String title) {
        List<ResGetNewsDto> newsList = newsService.searchNewsByTitle(title);
        return ResponseEntity.ok(newsList);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제하기", description = "게시글 번호로 게시글을 삭제합니다.")
    public ResponseEntity<String> deleteNewsPost(@PathVariable Long id) {
        newsService.deleteNewsPost(id);
        return ResponseEntity.ok("News Post Deleted Successfully!!");
    }
}
