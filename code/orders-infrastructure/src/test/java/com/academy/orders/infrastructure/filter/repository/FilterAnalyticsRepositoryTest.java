package com.academy.orders.infrastructure.filter.repository;

import com.academy.orders.domain.filter.FilterAnalyticsRepository;
import com.academy.prometheus_api.generated.api.FilterStatisticsApi;
import com.academy.prometheus_api.generated.model.MetricDTO;
import com.academy.prometheus_api.generated.model.MetricResultDTO;
import com.academy.prometheus_api.generated.model.MetricsDataDTO;
import com.academy.prometheus_api.generated.model.MetricsResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FilterAnalyticsRepositoryTest {
  @Mock
  private FilterStatisticsApi filterStatisticsApi;

  @InjectMocks
  private FilterAnalyticsRepositoryImpl filterAnalyticsRepository;

  @Test
  public void testGetFirstCounter_whenValidResponse() {
    final String query = "query";
    final String value = "11.003821153310913";

    final MetricDTO metricDTO = new MetricDTO();
    metricDTO.application("application");
    metricDTO.instance("https://google.com");
    metricDTO.job("Job");

    final MetricResultDTO metricResultDTO = new MetricResultDTO();
    metricResultDTO.metric(metricDTO);
    metricResultDTO.addValueItem("1742898337.591");
    metricResultDTO.addValueItem(value);

    final MetricsDataDTO metricsDataDTO = new MetricsDataDTO();
    metricsDataDTO.setResultType("vector");
    metricsDataDTO.setResult(List.of(metricResultDTO));

    final MetricsResponseDTO metricsResponseDTO = new MetricsResponseDTO();
    metricsResponseDTO.setStatus("success");
    metricsResponseDTO.setData(metricsDataDTO);

    when(filterStatisticsApi.getMetrics(query)).thenReturn(metricsResponseDTO);

    final Optional<Integer> firstCounter = filterAnalyticsRepository.getFirstCounter(query);

    assertEquals(Double.valueOf(value).intValue(), firstCounter.orElseThrow());
  }
}
