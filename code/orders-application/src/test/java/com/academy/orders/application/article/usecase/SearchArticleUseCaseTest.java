package com.academy.orders.application.article.usecase;

import com.academy.orders.domain.article.entity.Article;
import com.academy.orders.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.academy.orders.application.ModelUtils.getArticle;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchArticleUseCaseTest {
    @InjectMocks
    private SearchArticlesUseCaseImpl searchArticlesUseCase;

    @Mock
    private ArticleRepository articleRepository;

    @Test
    void searchArticlesTest() {
        final String query = "how";
        final String lang = LANGUAGE_EN;

        when(articleRepository.findByTitleOrContentIgnoreCase(query, lang))
                .thenReturn(List.of(getArticle()));

        final List<Article> result = searchArticlesUseCase.searchArticles(query, lang);
    }
}
