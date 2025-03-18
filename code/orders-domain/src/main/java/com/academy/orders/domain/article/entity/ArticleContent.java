package com.academy.orders.domain.article.entity;

import com.academy.orders.domain.product.entity.Language;
import lombok.Builder;

@Builder
public record ArticleContent(String title, String content, Language language) {
}
