package com.academy.orders.domain.analytics.usecase;

import com.academy.orders.domain.filter.dto.FilterUsageReportDto;

public interface ProductsOnSaleFilterWeeklyAnalyticsUseCase {
  FilterUsageReportDto getWeeklyStatistics(int amount);
}
