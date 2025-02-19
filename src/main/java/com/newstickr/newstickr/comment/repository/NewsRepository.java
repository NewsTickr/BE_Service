package com.newstickr.newstickr.comment.repository;

import com.newstickr.newstickr.comment.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
