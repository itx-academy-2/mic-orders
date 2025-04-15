package com.academy.orders.domain.article.usecase;

import com.academy.orders.domain.article.entity.Article;

import java.util.List;

/**
 * Use case for searching articles based on a query string. <p> This search looks for matches in both the article's <b>title</b> and
 * <b>content</b>, using case-insensitive partial matching. </p>
 */
public interface SearchArticlesUseCase {
  List<Article> searchArticles(String query, String language);
}
