package com.academy.orders.infrastructure.article.repository;

import com.academy.orders.infrastructure.article.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleJpaAdapter extends JpaRepository<ArticleEntity, Long> {
  @Query("""
      SELECT a
      FROM ArticleEntity a
      INNER JOIN FETCH a.contents c
      INNER JOIN FETCH c.language l
      WHERE a.id = :id AND l.code = :languageCode
      """)
  Optional<ArticleEntity> findByIdAndLanguageCode(Long id, String languageCode);

  @Query("""
      SELECT a
      FROM ArticleEntity a
      INNER JOIN FETCH a.contents c
      INNER JOIN FETCH c.language l
      WHERE l.code = :languageCode
      """)
  Page<ArticleEntity> findAllByLanguageCode(String languageCode, Pageable pageable);
}
