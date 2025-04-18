package com.academy.orders.apirest.articles.controller;

import com.academy.orders.apirest.articles.mapper.ArticleDTOResponseMapper;
import com.academy.orders.apirest.articles.mapper.PageArticleDetailsMapper;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.usecase.GetArticleByIdUseCase;
import com.academy.orders.domain.article.usecase.GetArticlesUseCase;
import com.academy.orders.domain.article.usecase.SearchArticlesUseCase;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders_api_rest.generated.api.ArticleApi;
import com.academy.orders_api_rest.generated.model.ArticleDetailsDTO;
import com.academy.orders_api_rest.generated.model.ArticleResponseDTO;
import com.academy.orders_api_rest.generated.model.PageArticleDetailsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController implements ArticleApi {
  private final GetArticleByIdUseCase getArticleByIdUseCase;

  private final GetArticlesUseCase getArticlesUseCase;

  private final SearchArticlesUseCase searchArticlesUseCase;

  private final ArticleDTOResponseMapper articleDTOResponseMapper;

  private final PageArticleDetailsMapper pageArticleDetailsMapper;

  private final PageableDTOMapper pageableDTOMapper;

  @Override
  public ResponseEntity<ArticleResponseDTO> getArticleById(Long articleId, String lang) {
    final Article article = getArticleByIdUseCase.getArticleById(articleId, lang);
    ArticleResponseDTO articleResponseDTO = articleDTOResponseMapper.toDto(article);
    return ResponseEntity.ok(articleResponseDTO);
  }

  @Override
  public ResponseEntity<PageArticleDetailsDTO> getArticlesDetails(String lang, PageableDTO pageableDto) {
    final Pageable pageable = pageableDTOMapper.fromDto(pageableDto);
    Page<Article> page = getArticlesUseCase.getArticles(lang, pageable);
    PageArticleDetailsDTO pageArticleDetailsDTO = pageArticleDetailsMapper.fromModel(page);
    return ResponseEntity.ok(pageArticleDetailsDTO);
  }

  @Override
  public ResponseEntity<List<ArticleDetailsDTO>> searchArticles(String query, String lang) {
    final List<ArticleDetailsDTO> list = searchArticlesUseCase.searchArticles(query, lang).stream()
        .map(pageArticleDetailsMapper::toArticleDetailsDTO)
        .toList();
    return ResponseEntity.ok(list);
  }
}
