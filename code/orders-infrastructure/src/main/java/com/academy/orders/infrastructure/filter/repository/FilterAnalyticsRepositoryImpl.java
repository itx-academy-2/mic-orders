package com.academy.orders.infrastructure.filter.repository;

import com.academy.prometheus_api.generated.api.FilterStatisticsApi;
import com.academy.prometheus_api.generated.model.MetricsResponseDTO;
import com.academy.orders.domain.filter.FilterAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilterAnalyticsRepositoryImpl implements FilterAnalyticsRepository {
  private final FilterStatisticsApi filterStatisticsApi;

  @Override
  public Optional<Integer> getFirstCounter(String query) {
    final MetricsResponseDTO metricsResponseDTO = filterStatisticsApi.getMetrics(query);

    if (metricsResponseDTO == null
        || metricsResponseDTO.getData() == null
        || metricsResponseDTO.getData().getResult().isEmpty()) {
      return Optional.empty();
    }
    final List<String> stringsValues = metricsResponseDTO.getData().getResult().get(0).getValue();

    return Optional.of(Double.valueOf(stringsValues.get(1)).intValue());
  }
}
