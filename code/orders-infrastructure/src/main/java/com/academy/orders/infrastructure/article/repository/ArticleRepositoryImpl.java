package com.academy.orders.infrastructure.article.repository;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.repository.ArticleRepository;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.infrastructure.article.entity.ArticleEntity;
import com.academy.orders.infrastructure.article.mapper.ArticleMapper;
import com.academy.orders.infrastructure.article.mapper.ArticlePageMapper;
import com.academy.orders.infrastructure.common.PageableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class ArticleRepositoryImpl implements ArticleRepository {
  private final ArticleJpaAdapter articleJpaAdapter;

  private final ArticleMapper articleMapper;

  private final ArticlePageMapper articlePageMapper;

  private final PageableMapper pageableMapper;

  @Override
  public Optional<Article> findByIdAndLanguageCode(Long id, String languageCode) {
    Optional<ArticleEntity> articleEntity = articleJpaAdapter.findByIdAndLanguageCode(id, languageCode);
    return articleEntity.map(articleMapper::fromEntity);
  }

  @Override
  public Page<Article> findAllArticlesByLanguage(String language, Pageable pageable) {
    final PageRequest pageRequest = pageableMapper.fromDomain(pageable);
    var articles = articleJpaAdapter.findAllByLanguageCode(language, pageRequest);
    return articlePageMapper.toDomain(articles);
  }
}
