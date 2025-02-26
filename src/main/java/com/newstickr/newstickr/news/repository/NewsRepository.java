package com.newstickr.newstickr.news.repository;

import com.newstickr.newstickr.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // 제목에 특정 키워드가 포함된 게시글 검색 (대소문자 구분 없이 LIKE 사용)
    @Query("SELECT n FROM News n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<News> findByTitleContainingIgnoreCase(@Param("title") String title);
    List<News> findAllByUser_Id(Long id);

    List<News> findAllByUserIdOrderByNewsIdDesc(Long userId);
    List<News> findAllByOrderByNewsIdDesc();
}
