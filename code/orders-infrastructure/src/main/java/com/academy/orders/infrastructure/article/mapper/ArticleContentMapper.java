package com.academy.orders.infrastructure.article.mapper;

import com.academy.orders.domain.article.entity.ArticleContent;
import com.academy.orders.infrastructure.article.entity.ArticleContentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleContentMapper {
  ArticleContent fromEntity(final ArticleContentEntity articleContent);
}
