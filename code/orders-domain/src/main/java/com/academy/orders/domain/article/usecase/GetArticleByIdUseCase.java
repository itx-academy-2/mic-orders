package com.academy.orders.domain.article.usecase;

import com.academy.orders.domain.article.entity.Article;

public interface GetArticleByIdUseCase {
  Article getArticleById(long id, String language);
}
