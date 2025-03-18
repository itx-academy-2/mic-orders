package com.academy.orders.infrastructure.article.entity;

import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "article_contents")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ArticleContentEntity {
  @EmbeddedId
  private ArticleContentId articleContentId;

  private String title;

  private String content;

  @ManyToOne
  @MapsId("articleId")
  private ArticleEntity article;

  @ManyToOne
  @MapsId("languageId")
  private LanguageEntity language;
}
