package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.usecase.ProductFilterUsageMetricsUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductFilterUsageMetricsUseCaseImpl implements ProductFilterUsageMetricsUseCase {
  private final Counter tagCounter;

  private final Counter minimumDiscountCounter;

  private final Counter maximumDiscountCounter;

  private final Counter minimumPriceWithDiscountCounter;

  private final Counter maximumPriceWithDiscountCounter;

  public ProductFilterUsageMetricsUseCaseImpl(MeterRegistry meterRegistry) {
    tagCounter = Counter.builder("tag_filter_usage")
        .description("""
            This filter tracks and defines the metrics used by the product filter when searching for products on sale
            """)
        .register(meterRegistry);

    minimumDiscountCounter = Counter.builder("minimum_discount_filter_usage")
        .description("""
            This filter tracks the usage of the minimum discount filter when searching for products on sale
            """)
        .register(meterRegistry);

    maximumDiscountCounter = Counter.builder("maximum_discount_filter_usage")
        .description("""
            This filter tracks the usage of the maximum discount filter when searching for products on sale
            """)
        .register(meterRegistry);

    minimumPriceWithDiscountCounter = Counter.builder("minimum_price_with_discount_filter_usage")
        .description("""
            This filter tracks the usage of the minimum price with discount filter when searching for products on sale
            """)
        .register(meterRegistry);

    maximumPriceWithDiscountCounter = Counter.builder("maximum_price_with_discount_filter_usage")
        .description("""
            This filter tracks the usage of the maximum price with discount filter when searching for products on sale
            """)
        .register(meterRegistry);
  }

  @Override
  public void addMetrics(final ProductsOnSaleFilterDto filter) {
    if (Objects.isNull(filter)) {
      return;
    }
    if (Objects.nonNull(filter.tags())) {
      tagCounter.increment();
    }
    if (Objects.nonNull(filter.minimumDiscount())) {
      minimumDiscountCounter.increment();
    }
    if (Objects.nonNull(filter.maximumDiscount())) {
      maximumDiscountCounter.increment();
    }
    if (Objects.nonNull(filter.minimumPriceWithDiscount())) {
      minimumPriceWithDiscountCounter.increment();
    }
    if (Objects.nonNull(filter.maximumPriceWithDiscount())) {
      maximumPriceWithDiscountCounter.increment();
    }
  }
}
