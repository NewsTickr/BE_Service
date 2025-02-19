package com.newstickr.newstickr.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long newsId;
    private String link;
    private String title;
    private String description;
    private String pubDate;
    private boolean deleted;
    private String sentiment;
    private String originalLink;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
