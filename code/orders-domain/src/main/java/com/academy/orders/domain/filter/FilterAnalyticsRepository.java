package com.academy.orders.domain.filter;

import java.util.Optional;

public interface FilterAnalyticsRepository {
  Optional<Integer> getFirstCounter(String query);
}
