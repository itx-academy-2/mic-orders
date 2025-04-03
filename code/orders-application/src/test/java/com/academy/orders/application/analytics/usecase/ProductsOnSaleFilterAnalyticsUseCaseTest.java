package com.academy.orders.application.analytics.usecase;

import com.academy.orders.domain.filter.FilterAnalyticsRepository;
import com.academy.orders.domain.filter.dto.FilterUsageReportDto;
import com.academy.orders.domain.filter.dto.FilterUsageStatisticsDto;
import com.academy.orders.domain.filter.dto.PeriodFilterUsageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductsOnSaleFilterAnalyticsUseCaseTest {
  @Mock
  private FilterAnalyticsRepository filterAnalyticsRepository;

  private ProductsOnSaleFilterWeeklyAnalyticsUseCaseImpl productsOnSaleFilterWeeklyAnalyticsUseCase;

  private final String stage = "stage";

  @BeforeEach
  public void setUp() {
    this.productsOnSaleFilterWeeklyAnalyticsUseCase = new ProductsOnSaleFilterWeeklyAnalyticsUseCaseImpl(
        filterAnalyticsRepository, stage);
  }

  @Test
  public void getWeeklyStatisticsTest() {
    final int amount = 3;
    final int differenceBetweenStartAndEndOfWeek = DayOfWeek.SUNDAY.getValue() - DayOfWeek.MONDAY.getValue();

    when(filterAnalyticsRepository
        .getFirstCounter(matches("increase\\(tag_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)")))
            .thenReturn(Optional.of(1));
    when(filterAnalyticsRepository
        .getFirstCounter(matches("increase\\(minimum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)")))
            .thenReturn(Optional.of(2));
    when(filterAnalyticsRepository
        .getFirstCounter(matches("increase\\(maximum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)")))
            .thenReturn(Optional.of(3));
    when(filterAnalyticsRepository
        .getFirstCounter(
            matches("increase\\(minimum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)")))
                .thenReturn(Optional.of(4));
    when(filterAnalyticsRepository
        .getFirstCounter(
            matches("increase\\(maximum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)")))
                .thenReturn(Optional.of(5));
    when(filterAnalyticsRepository
        .getFirstCounter(matches("increase\\(tag_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)")))
            .thenReturn(Optional.of(1));
    when(filterAnalyticsRepository.getFirstCounter(
        matches("increase\\(minimum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)")))
            .thenReturn(Optional.of(2));
    when(filterAnalyticsRepository.getFirstCounter(
        matches("increase\\(maximum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)")))
            .thenReturn(Optional.of(3));
    when(filterAnalyticsRepository
        .getFirstCounter(matches(
            "increase\\(minimum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)")))
                .thenReturn(Optional.of(4));
    when(filterAnalyticsRepository
        .getFirstCounter(matches(
            "increase\\(maximum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)")))
                .thenReturn(Optional.of(5));

    final FilterUsageReportDto result = productsOnSaleFilterWeeklyAnalyticsUseCase.getWeeklyStatistics(amount);

    assertNotNull(result);
    assertEquals(5, result.getFilterMetrics().size(), "Wrong number of filter metrics");

    for (int i = 0; i < result.getFilterMetrics().size(); i++) {
      final FilterUsageStatisticsDto filterUsageStatisticsDto = result.getFilterMetrics().get(i);
      assertNotNull(filterUsageStatisticsDto);
      assertEquals((i + 1) * amount, filterUsageStatisticsDto.getSumOfAllCounts());

      final List<PeriodFilterUsageDto> periodFilterUsageDtoList = filterUsageStatisticsDto.getMetrics();
      assertNotNull(periodFilterUsageDtoList);
      assertEquals(amount, periodFilterUsageDtoList.size());

      for (int j = 0; j < periodFilterUsageDtoList.size(); j++) {
        final PeriodFilterUsageDto periodFilterUsageDto = periodFilterUsageDtoList.get(j);
        assertNotNull(periodFilterUsageDto);
        assertEquals(i + 1, periodFilterUsageDto.getCount());
        if (j != 0) {
          assertEquals(6, ChronoUnit.DAYS.between(periodFilterUsageDto.getStartDate(), periodFilterUsageDto.getEndDate()));
          assertEquals(DayOfWeek.MONDAY, periodFilterUsageDto.getStartDate().getDayOfWeek());
          assertEquals(DayOfWeek.SUNDAY, periodFilterUsageDto.getEndDate().getDayOfWeek());
        } else {
          final long daysBetween = ChronoUnit.DAYS.between(periodFilterUsageDto.getStartDate(),
              periodFilterUsageDto.getEndDate());
          assertTrue(daysBetween <= differenceBetweenStartAndEndOfWeek);
        }
      }
    }

    verify(filterAnalyticsRepository)
        .getFirstCounter(matches("increase\\(tag_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)"));
    verify(filterAnalyticsRepository)
        .getFirstCounter(matches("increase\\(minimum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)"));
    verify(filterAnalyticsRepository)
        .getFirstCounter(matches("increase\\(maximum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)"));
    verify(filterAnalyticsRepository)
        .getFirstCounter(
            matches("increase\\(minimum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)"));
    verify(filterAnalyticsRepository)
        .getFirstCounter(
            matches("increase\\(maximum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+m\\]\\)"));
    verify(filterAnalyticsRepository, times(2))
        .getFirstCounter(matches("increase\\(tag_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)"));
    verify(filterAnalyticsRepository, times(2))
        .getFirstCounter(
            matches("increase\\(minimum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)"));
    verify(filterAnalyticsRepository, times(2))
        .getFirstCounter(
            matches("increase\\(maximum_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)"));
    verify(filterAnalyticsRepository, times(2))
        .getFirstCounter(matches(
            "increase\\(minimum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)"));
    verify(filterAnalyticsRepository, times(2))
        .getFirstCounter(matches(
            "increase\\(maximum_price_with_discount_filter_usage_total\\{application=\"" + stage + "\"\\}\\[\\d+w\\] offset\\s*\\d+m\\)"));
  }

}
