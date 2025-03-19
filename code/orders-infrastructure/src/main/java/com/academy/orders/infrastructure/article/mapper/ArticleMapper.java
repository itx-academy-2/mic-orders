package com.academy.orders.infrastructure.article.mapper;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.infrastructure.article.entity.ArticleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
  Article fromEntity(ArticleEntity articleEntity);
}
