package com.academy.orders.domain.article.repository;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;

import java.util.Optional;

public interface ArticleRepository {
  Optional<Article> findByIdAndLanguageCode(Long id, String languageCode);

  Page<Article> findAllArticlesByLanguage(String language, Pageable pageable);
}
