package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.exception.ArticleNotFoundException;
import com.academy.orders.domain.article.repository.ArticleRepository;
import com.academy.orders.domain.article.usecase.GetArticleByIdUseCase;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetArticleByIdUseCaseImpl implements GetArticleByIdUseCase {
  private final ArticleRepository articleRepository;

  private final LanguageRepository languageRepository;

  @Override
  public Article getArticleById(long id, String language) {
    languageRepository.findByCode(language).orElseThrow(() -> new LanguageNotFoundException(language));
    return articleRepository.findByIdAndLanguageCode(id, language)
        .orElseThrow(() -> new ArticleNotFoundException(id));
  }
}
