package com.academy.orders.infrastructure.article.repository;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.infrastructure.article.entity.ArticleEntity;
import com.academy.orders.infrastructure.article.mapper.ArticleMapper;
import com.academy.orders.infrastructure.article.mapper.ArticlePageMapper;
import com.academy.orders.infrastructure.common.PageableMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.academy.orders.infrastructure.ModelUtils.getArticle;
import static com.academy.orders.infrastructure.ModelUtils.getArticleEntity;
import static com.academy.orders.infrastructure.ModelUtils.getPage;
import static com.academy.orders.infrastructure.ModelUtils.getPageImplOf;
import static com.academy.orders.infrastructure.ModelUtils.getPageOf;
import static com.academy.orders.infrastructure.ModelUtils.getPageRequest;
import static com.academy.orders.infrastructure.ModelUtils.getPageable;
import static com.academy.orders.infrastructure.TestConstants.LANGUAGE_EN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleRepositoryTest {
  @Mock
  private ArticleJpaAdapter articleJpaAdapter;

  @Mock
  private ArticleMapper articleMapper;

  @Mock
  private ArticlePageMapper articlePageMapper;

  @Mock
  private PageableMapper pageableMapper;

  @InjectMocks
  private ArticleRepositoryImpl articleRepository;

  @Test
  public void findByIdAndLanguageCodeWhenNotFoundTest() {
    final long id = 1L;
    final String languageCode = LANGUAGE_EN;

    when(articleJpaAdapter.findByIdAndLanguageCode(id, languageCode))
        .thenReturn(Optional.empty());

    articleRepository.findByIdAndLanguageCode(id, languageCode);

    verify(articleJpaAdapter).findByIdAndLanguageCode(id, languageCode);
    verify(articleMapper, never()).fromEntity(any(ArticleEntity.class));
  }

  @Test
  public void findByIdAndLanguageCodeTest() {
    final long id = 1L;
    final String languageCode = LANGUAGE_EN;
    final ArticleEntity articleEntity = getArticleEntity();
    final Article article = getArticle();

    when(articleJpaAdapter.findByIdAndLanguageCode(id, languageCode))
        .thenReturn(Optional.of(articleEntity));
    when(articleMapper.fromEntity(articleEntity)).thenReturn(article);

    articleRepository.findByIdAndLanguageCode(id, languageCode);

    verify(articleJpaAdapter).findByIdAndLanguageCode(id, languageCode);
    verify(articleMapper).fromEntity(articleEntity);
  }

  @Test
  public void findAllArticlesByLanguage() {
    final String languageCode = LANGUAGE_EN;
    final Pageable pageable = getPageable();
    final PageRequest pageRequest = getPageRequest();
    final PageImpl<ArticleEntity> articlePage = getPageImplOf(getArticleEntity());
    final Page<Article> expected = getPage(List.of(getArticle()), 1, 1, 1, 1);

    when(pageableMapper.fromDomain(pageable)).thenReturn(pageRequest);
    when(articleJpaAdapter.findAllByLanguageCode(languageCode, pageRequest)).thenReturn(articlePage);
    when(articlePageMapper.toDomain(articlePage)).thenReturn(expected);

    final Page<Article> result = articleRepository.findAllArticlesByLanguage(languageCode, pageable);

    assertEquals(expected, result);

    verify(pageableMapper).fromDomain(pageable);
    verify(articleJpaAdapter).findAllByLanguageCode(languageCode, pageRequest);
    verify(articlePageMapper).toDomain(articlePage);
  }

  @Test
  void findByTitleOrContentIgnoreCaseTest() {
    final String query = "how";
    final String lang = LANGUAGE_EN;
    final ArticleEntity articleEntity = getArticleEntity();
    final List<ArticleEntity> articleEntities = List.of(articleEntity);
    final Article article = getArticle();

    when(articleJpaAdapter.findAllByTitleOrContentIgnoreCase(query, lang))
        .thenReturn(articleEntities);
    when(articleMapper.fromEntity(articleEntity)).thenReturn(article);

    final List<Article> result = articleRepository.findByTitleOrContentIgnoreCase(query, lang);

    assertNotNull(result);
    assertIterableEquals(List.of(article), result);

    verify(articleJpaAdapter).findAllByTitleOrContentIgnoreCase(query, lang);
    verify(articleMapper, times(articleEntities.size())).fromEntity(articleEntity);
  }
}
