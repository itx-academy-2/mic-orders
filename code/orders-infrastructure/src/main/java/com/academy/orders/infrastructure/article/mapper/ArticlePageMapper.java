package com.academy.orders.infrastructure.article.mapper;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.infrastructure.article.entity.ArticleEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ArticlePageMapper {
  com.academy.orders.domain.common.Page<Article> toDomain(Page<ArticleEntity> page);
}
