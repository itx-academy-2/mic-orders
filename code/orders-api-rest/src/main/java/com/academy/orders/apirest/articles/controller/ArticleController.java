package com.academy.orders.apirest.articles.controller;

import com.academy.orders.apirest.articles.mapper.ArticleDTOResponseMapper;
import com.academy.orders.apirest.articles.mapper.PageArticleDetailsMapper;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.usecase.GetArticleByIdUseCase;
import com.academy.orders.domain.article.usecase.GetArticlesUseCase;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders_api_rest.generated.api.ArticleApi;
import com.academy.orders_api_rest.generated.model.ArticleResponseDTO;
import com.academy.orders_api_rest.generated.model.PageArticleDetailsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController implements ArticleApi {
  private final GetArticleByIdUseCase getArticleByIdUseCase;

  private final GetArticlesUseCase getArticlesUseCase;

  private final ArticleDTOResponseMapper articleDTOResponseMapper;

  private final PageArticleDetailsMapper pageArticleDetailsMapper;

  private final PageableDTOMapper pageableDTOMapper;

  @Override
  public ArticleResponseDTO getArticleById(Long articleId, String lang) {
    final Article article = getArticleByIdUseCase.getArticleById(articleId, lang);
    return articleDTOResponseMapper.toDto(article);
  }

  @Override
  public PageArticleDetailsDTO getArticlesDetails(String lang, PageableDTO pageableDto) {
    final Pageable pageable = pageableDTOMapper.fromDto(pageableDto);
    Page<Article> page = getArticlesUseCase.getArticles(lang, pageable);
    return pageArticleDetailsMapper.fromModel(page);
  }
}
