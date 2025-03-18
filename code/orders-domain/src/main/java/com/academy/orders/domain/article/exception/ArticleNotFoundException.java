package com.academy.orders.domain.article.exception;

import com.academy.orders.domain.common.exception.NotFoundException;
import lombok.Getter;

@Getter
public class ArticleNotFoundException extends NotFoundException {
  private final long articleId;

  public ArticleNotFoundException(final long articleId) {
    super(String.format("Cannot find article with id %d", articleId));
    this.articleId = articleId;
  }
}
