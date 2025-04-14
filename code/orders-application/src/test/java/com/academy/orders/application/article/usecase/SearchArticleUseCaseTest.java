package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.academy.orders.application.ModelUtils.getArticle;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchArticleUseCaseTest {
  @InjectMocks
  private SearchArticlesUseCaseImpl searchArticlesUseCase;

  @Mock
  private ArticleRepository articleRepository;

  @Test
  void searchArticlesTest() {
    final String query = "how";
    final String lang = LANGUAGE_EN;
    final List<Article> articles = List.of(getArticle());

    when(articleRepository.findByTitleOrContentIgnoreCase(query, lang))
        .thenReturn(articles);

    final List<Article> result = searchArticlesUseCase.searchArticles(query, lang);

    assertNotNull(result);
    assertIterableEquals(articles, result);

    verify(articleRepository).findByTitleOrContentIgnoreCase(query, lang);
  }
}
