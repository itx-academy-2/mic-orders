package com.academy.orders.domain.article.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Article {
  private Long id;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private Set<ArticleContent> contents;
}
