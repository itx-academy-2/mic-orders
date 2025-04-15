package com.academy.orders.apirest.article.controller;

import com.academy.orders.apirest.articles.controller.ArticleController;
import com.academy.orders.apirest.articles.mapper.ArticleDTOResponseMapper;
import com.academy.orders.apirest.articles.mapper.PageArticleDetailsMapper;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.exception.ArticleNotFoundException;
import com.academy.orders.domain.article.usecase.GetArticleByIdUseCase;
import com.academy.orders.domain.article.usecase.GetArticlesUseCase;
import com.academy.orders.domain.article.usecase.SearchArticlesUseCase;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders_api_rest.generated.model.ArticleDetailsDTO;
import com.academy.orders_api_rest.generated.model.ArticleResponseDTO;
import com.academy.orders_api_rest.generated.model.PageArticleDetailsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.academy.orders.apirest.ModelUtils.*;
import static com.academy.orders.apirest.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticleController.class)
@ContextConfiguration(classes = {ArticleController.class})
@Import({TestSecurityConfig.class, ErrorHandler.class, AopAutoConfiguration.class})
class ArticleControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ArticleController articleController;

  @MockBean
  private GetArticleByIdUseCase getArticleByIdUseCase;

  @MockBean
  private GetArticlesUseCase getArticlesUseCase;

  @MockBean
  private ArticleDTOResponseMapper articleDTOResponseMapper;

  @MockBean
  private PageArticleDetailsMapper pageArticleDetailsMapper;

  @MockBean
  private PageableDTOMapper pageableDTOMapper;

  @MockBean
  private SearchArticlesUseCase searchArticlesUseCase;

  @Test
  @SneakyThrows
  void getArticleByIdWhenArticleDoesNotExistTest() {
    final long articleId = 1L;

    when(getArticleByIdUseCase.getArticleById(articleId, LANGUAGE_UK))
        .thenThrow(ArticleNotFoundException.class);

    mockMvc.perform(get(GET_ARTICLE_BY_ID_URL, articleId)
        .param("lang", LANGUAGE_UK)
        .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(getArticleByIdUseCase).getArticleById(articleId, LANGUAGE_UK);
    verify(articleDTOResponseMapper, never()).toDto(any());
  }

  @Test
  @SneakyThrows
  void getArticleByIdTest() {
    final long articleId = 1L;
    final Article article = getArticle();
    final ArticleResponseDTO articleResponseDTO = getArticleResponseDTOInEnglish();

    when(getArticleByIdUseCase.getArticleById(articleId, LANGUAGE_EN))
        .thenReturn(article);
    when(articleDTOResponseMapper.toDto(article)).thenReturn(articleResponseDTO);

    final MvcResult result = mockMvc.perform(get(GET_ARTICLE_BY_ID_URL, articleId)
        .param("lang", LANGUAGE_EN)
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    assertEquals(objectMapper.writeValueAsString(articleResponseDTO), result.getResponse().getContentAsString());

    verify(getArticleByIdUseCase).getArticleById(articleId, LANGUAGE_EN);
    verify(articleDTOResponseMapper).toDto(article);
  }

  @Test
  @SneakyThrows
  void getArticlesTest() {
    final PageableDTO pageableDTO = getPageableDTO();
    final Pageable pageable = getPageable();
    final Page<Article> articles = getArticlesPage();
    final PageArticleDetailsDTO pageArticleDetailsDTO = getPageArticleDetailsDTOInEnglish();

    when(pageableDTOMapper.fromDto(pageableDTO)).thenReturn(pageable);
    when(getArticlesUseCase.getArticles(LANGUAGE_EN, pageable)).thenReturn(articles);
    when(pageArticleDetailsMapper.fromModel(articles)).thenReturn(pageArticleDetailsDTO);

    mockMvc.perform(get(GET_ARTICLES_DETAILS_URL)
        .param("lang", LANGUAGE_EN)
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(pageArticleDetailsMapper).fromModel(articles);
    verify(getArticlesUseCase).getArticles(LANGUAGE_EN, pageable);
    verify(pageArticleDetailsMapper).fromModel(articles);
  }

  @Test
  @SneakyThrows
  void searchArticlesTest() {
    final String query = "how";
    final String lang = LANGUAGE_EN;
    final Article article = getArticle();
    final List<Article> list = List.of(article);
    final ArticleDetailsDTO articleDetailsDTO = getArticleDetailsDTO();

    when(searchArticlesUseCase.searchArticles(query, lang))
        .thenReturn(list);
    when(pageArticleDetailsMapper.toArticleDetailsDTO(article)).thenReturn(articleDetailsDTO);

    final MvcResult mvcResult = mockMvc.perform(get(SEARCH_ARTICLES_URL)
        .param("lang", lang)
        .param("query", query)
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    assertEquals(objectMapper.writeValueAsString(List.of(articleDetailsDTO)), mvcResult.getResponse().getContentAsString());

    verify(searchArticlesUseCase).searchArticles(query, lang);
    verify(pageArticleDetailsMapper).toArticleDetailsDTO(article);
  }
}
