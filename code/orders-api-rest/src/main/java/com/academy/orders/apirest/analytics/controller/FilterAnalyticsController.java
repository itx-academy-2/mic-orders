package com.academy.orders.apirest.analytics.controller;

import com.academy.orders.apirest.analytics.mapper.FilterUsageReportDTOMapper;
import com.academy.orders.domain.analytics.usecase.ProductsOnSaleFilterWeeklyAnalyticsUseCase;
import com.academy.orders.domain.filter.dto.FilterUsageReportDto;
import com.academy.orders_api_rest.generated.api.FilterAnalyticsApi;
import com.academy.orders_api_rest.generated.model.FilterUsageReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FilterAnalyticsController implements FilterAnalyticsApi {
  private final ProductsOnSaleFilterWeeklyAnalyticsUseCase getWeeklyProductSaleFilterUsageStatisticsUseCase;

  private final FilterUsageReportDTOMapper filterUsageReportDTOMapper;

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
  @Override
  public FilterUsageReportDTO getWeeklyProductOnSaleFilterUsageStatistics(final Integer amount) {
    final FilterUsageReportDto dto = getWeeklyProductSaleFilterUsageStatisticsUseCase.getWeeklyStatistics(amount);
    return filterUsageReportDTOMapper.fromModel(dto);
  }
}
