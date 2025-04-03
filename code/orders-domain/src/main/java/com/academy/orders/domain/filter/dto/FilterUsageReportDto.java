package com.academy.orders.domain.filter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class FilterUsageReportDto {
  private List<FilterUsageStatisticsDto> filterMetrics;
}
