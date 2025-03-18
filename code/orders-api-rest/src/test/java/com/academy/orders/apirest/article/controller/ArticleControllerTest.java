package com.academy.orders.apirest.article.controller;

import com.academy.orders.apirest.articles.controller.ArticleController;
import com.academy.orders.apirest.articles.mapper.ArticleDTOResponseMapper;
import com.academy.orders.apirest.articles.mapper.PageArticleDetailsMapper;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.domain.article.exception.ArticleNotFoundException;
import com.academy.orders.domain.article.usecase.GetArticleByIdUseCase;
import com.academy.orders.domain.article.usecase.GetArticlesUseCase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.academy.orders.apirest.TestConstants.GET_ARTICLE_BY_ID_URL;
import static com.academy.orders.apirest.TestConstants.LANGUAGE_UK;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticleController.class)
@ContextConfiguration(classes = {ArticleController.class})
@Import(TestSecurityConfig.class)
public class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

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

    @Test
    @SneakyThrows
    public void getArticleByIdWhenArticleDoesNotExistTest() {
        final long articleId = 1L;

        when(getArticleByIdUseCase.getArticleById(articleId, LANGUAGE_UK))
                .thenThrow(ArticleNotFoundException.class);

        mockMvc.perform(get(GET_ARTICLE_BY_ID_URL, articleId)
                .param("lang", LANGUAGE_UK)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
