package com.academy.orders.apirest.articles.mapper;

import com.academy.orders.apirest.common.mapper.LocalDateTimeMapper;
import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.entity.ArticleContent;
import com.academy.orders_api_rest.generated.model.ArticleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LocalDateTimeMapper.class})
public interface ArticleDTOResponseMapper {
  @Mapping(target = "title", source = "contents", qualifiedByName = "mapTitle")
  @Mapping(target = "content", source = "contents", qualifiedByName = "mapContent")
  ArticleResponseDTO toDto(Article article);

  @Named("mapTitle")
  default String mapTitle(final List<ArticleContent> contents) {
    return contents.stream().map(ArticleContent::title)
        .findFirst()
        .orElse(null);
  }

  @Named("mapContent")
  default String mapContent(final List<ArticleContent> contents) {
    return contents.stream().map(ArticleContent::content)
        .findFirst()
        .orElse(null);
  }
};
