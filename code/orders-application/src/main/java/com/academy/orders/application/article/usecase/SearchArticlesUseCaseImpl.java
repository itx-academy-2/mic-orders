package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.repository.ArticleRepository;
import com.academy.orders.domain.article.usecase.SearchArticlesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchArticlesUseCaseImpl implements SearchArticlesUseCase {
  private final ArticleRepository articleRepository;

  @Override
  public List<Article> searchArticles(String query, String language) {
    return articleRepository.findByTitleOrContentIgnoreCase(query, language);
  }
}
