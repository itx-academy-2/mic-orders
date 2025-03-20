package com.academy.orders.apirest.articles.mapper;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.common.Page;
import com.academy.orders_api_rest.generated.model.ArticleDetailsDTO;
import com.academy.orders_api_rest.generated.model.PageArticleDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ArticleDTOResponseMapper.class)
public interface PageArticleDetailsMapper {
  @Mapping(target = "title", source = "contents", qualifiedByName = "mapTitle")
  ArticleDetailsDTO toArticleDetailsDTO(Article article);

  PageArticleDetailsDTO fromModel(Page<Article> articlePage);
}
