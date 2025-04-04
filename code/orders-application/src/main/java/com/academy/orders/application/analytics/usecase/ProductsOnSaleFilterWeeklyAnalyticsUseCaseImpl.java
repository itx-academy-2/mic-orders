package com.academy.orders.application.analytics.usecase;

import com.academy.orders.domain.analytics.usecase.ProductsOnSaleFilterWeeklyAnalyticsUseCase;
import com.academy.orders.domain.filter.FilterAnalyticsRepository;
import com.academy.orders.domain.filter.dto.FilterUsageReportDto;
import com.academy.orders.domain.filter.dto.FilterUsageStatisticsDto;
import com.academy.orders.domain.filter.dto.PeriodFilterUsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductsOnSaleFilterWeeklyAnalyticsUseCaseImpl implements ProductsOnSaleFilterWeeklyAnalyticsUseCase {
  private final FilterAnalyticsRepository filterAnalyticsRepository;

  private final String prometheusPart;

  public ProductsOnSaleFilterWeeklyAnalyticsUseCaseImpl(FilterAnalyticsRepository filterAnalyticsRepository,
      @Value("${prometheus.stage}") String prometheusStage) {
    this.filterAnalyticsRepository = filterAnalyticsRepository;
    this.prometheusPart = "{application=\"" + prometheusStage + "\"}";
  }

  @Override
  @Cacheable(value = "week-filter-statistics", key = "#amount")
  public FilterUsageReportDto getWeeklyStatistics(int amount) {
    final List<FilterUsageStatisticsDto> list = List.of(
        getStatisticsQuery("tag_filter_usage_total", amount),
        getStatisticsQuery("minimum_discount_filter_usage_total", amount),
        getStatisticsQuery("maximum_discount_filter_usage_total", amount),
        getStatisticsQuery("minimum_price_with_discount_filter_usage_total", amount),
        getStatisticsQuery("maximum_price_with_discount_filter_usage_total", amount));

    return FilterUsageReportDto.builder()
        .filterMetrics(list)
        .build();
  }

  @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
  @CacheEvict(value = "week-filter-statistics", allEntries = true, beforeInvocation = true)
  public void evictCache() {
    log.info("Cache evicted for week filter statistics at {}", LocalDateTime.now());
  }

  private FilterUsageStatisticsDto getStatisticsQuery(final String filterName, final int amount) {
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime mondayOfCurrentWeek = now.toLocalDate()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .atStartOfDay();
    final long difference = ChronoUnit.MINUTES.between(mondayOfCurrentWeek, now);
    int sum = 0;

    final List<PeriodFilterUsageDto> periodFilterUsageDtoList = new LinkedList<>();

    for (int i = 0; i < amount; i++) {
      String query = null;
      PeriodFilterUsageDto periodFilterUsageDto = new PeriodFilterUsageDto();

      if (i == 0) {
        query = "increase(" + filterName + prometheusPart + "[" + difference + "m" + "])";

        periodFilterUsageDto.setStartDate(mondayOfCurrentWeek.toLocalDate());
        periodFilterUsageDto.setEndDate(now.toLocalDate());
      } else {
        query = "increase(" + filterName + prometheusPart + "[1w] offset " + (difference + (10080L * (i - 1))) + "m)";

        final LocalDate from = mondayOfCurrentWeek.toLocalDate().minusWeeks(i);
        periodFilterUsageDto.setStartDate(from);
        periodFilterUsageDto.setEndDate(from.plusDays(6));
      }
      final Optional<Integer> count = filterAnalyticsRepository.getFirstCounter(query);

      periodFilterUsageDto.setCount(count.orElse(null));
      periodFilterUsageDtoList.add(periodFilterUsageDto);

      sum += (periodFilterUsageDto.getCount() == null) ? 0 : periodFilterUsageDto.getCount();
      log.info("Sent to Prometheus {}", query);
    }

    return FilterUsageStatisticsDto.builder()
        .filterName(filterName)
        .sumOfAllCounts(sum)
        .metrics(periodFilterUsageDtoList)
        .build();
  }
}
