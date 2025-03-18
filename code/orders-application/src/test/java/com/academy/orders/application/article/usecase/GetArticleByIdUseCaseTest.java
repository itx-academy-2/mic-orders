package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.exception.ArticleNotFoundException;
import com.academy.orders.domain.article.repository.ArticleRepository;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.academy.orders.application.ModelUtils.getArticle;
import static com.academy.orders.application.ModelUtils.getLanguage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class GetArticleByIdUseCaseTest {
  @Mock
  private ArticleRepository productRepository;

  @Mock
  private LanguageRepository languageRepository;

  @InjectMocks
  private GetArticleByIdUseCaseImpl getArticleByIdUseCase;

  @Test
  void getArticleByIdWhenLanguageIsNotFoundTest() {
    final String languageCode = "en";
    final long articleId = 1L;

    when(languageRepository.findByCode(languageCode)).thenReturn(Optional.empty());

    assertThrows(LanguageNotFoundException.class, () -> getArticleByIdUseCase.getArticleById(articleId, languageCode));

    verify(languageRepository).findByCode(languageCode);
    verify(productRepository, never()).findByIdAndLanguageCode(articleId, languageCode);
  }

  @Test
  void getArticleByIdWhenArticleIsNotFoundTest() {
    final String languageCode = "en";
    final long articleId = 1L;

    when(languageRepository.findByCode(languageCode)).thenReturn(Optional.of(getLanguage()));
    when(productRepository.findByIdAndLanguageCode(articleId, languageCode)).thenReturn(Optional.empty());

    assertThrows(ArticleNotFoundException.class, () -> getArticleByIdUseCase.getArticleById(articleId, languageCode));

    verify(languageRepository).findByCode(languageCode);
    verify(productRepository).findByIdAndLanguageCode(articleId, languageCode);
  }

  @Test
  void getArticleByIdWhenArticleIsFoundTest() {
    final String languageCode = "en";
    final long articleId = 1L;
    final Article article = getArticle();

    when(languageRepository.findByCode(languageCode)).thenReturn(Optional.of(getLanguage()));
    when(productRepository.findByIdAndLanguageCode(articleId, languageCode)).thenReturn(Optional.of(
        article));

    final Article result = getArticleByIdUseCase.getArticleById(articleId, languageCode);
    assertEquals(article, result);

    verify(languageRepository).findByCode(languageCode);
    verify(productRepository).findByIdAndLanguageCode(articleId, languageCode);
  }

}
