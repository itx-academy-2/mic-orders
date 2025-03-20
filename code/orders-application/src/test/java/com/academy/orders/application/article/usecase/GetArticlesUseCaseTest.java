package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.repository.ArticleRepository;
import com.academy.orders.domain.article.usecase.GetArticlesUseCase;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.academy.orders.application.ModelUtils.getArticle;
import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getPageable;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetArticlesUseCaseTest {
  @Mock
  private ArticleRepository articleRepository;

  @InjectMocks
  private GetArticlesUseCaseImpl getArticlesUseCase;

  @Test
  public void getArticlesTest() {
    final String language = LANGUAGE_EN;
    final Pageable pageable = getPageable();
    final Page<Article> page = getPage(List.of(getArticle()), 1, 1, 1, 1);

    when(articleRepository.findAllArticlesByLanguage(language, pageable))
        .thenReturn(page);

    final Page<Article> result = getArticlesUseCase.getArticles(language, pageable);

    assertEquals(page, result);
    verify(articleRepository).findAllArticlesByLanguage(language, pageable);
  }
}
