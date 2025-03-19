package com.academy.orders.domain.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;

public interface GetArticlesUseCase {
  Page<Article> getArticles(String language, Pageable pageable);
}
