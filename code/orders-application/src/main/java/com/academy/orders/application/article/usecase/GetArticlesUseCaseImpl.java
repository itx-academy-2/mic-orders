package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.repository.ArticleRepository;
import com.academy.orders.domain.article.usecase.GetArticlesUseCase;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetArticlesUseCaseImpl implements GetArticlesUseCase {
  private final ArticleRepository articleRepository;

  @Override
  public Page<Article> getArticles(String language, Pageable pageable) {

    return articleRepository.findAllArticlesByLanguage(language, pageable);
  }
}
