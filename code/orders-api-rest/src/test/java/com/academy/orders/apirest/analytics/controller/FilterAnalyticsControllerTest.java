package com.academy.orders.apirest.analytics.controller;

import com.academy.orders.apirest.analytics.mapper.FilterUsageReportDTOMapper;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.domain.analytics.usecase.ProductsOnSaleFilterWeeklyAnalyticsUseCase;
import com.academy.orders.domain.filter.dto.FilterUsageReportDto;
import com.academy.orders_api_rest.generated.model.FilterUsageReportDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.academy.orders.apirest.ModelUtils.getFilterUsageReportDTO;
import static com.academy.orders.apirest.ModelUtils.getFilterUsageReportDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilterAnalyticsController.class)
@ContextConfiguration(classes = {FilterAnalyticsController.class})
@Import({AopAutoConfiguration.class, TestSecurityConfig.class, ErrorHandler.class})
public class FilterAnalyticsControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProductsOnSaleFilterWeeklyAnalyticsUseCase productsOnSaleFilterWeeklyAnalyticsUseCase;

  @MockBean
  private FilterUsageReportDTOMapper filterUsageReportDTOMapper;

  @SneakyThrows
  @Test
  public void getWeeklyProductOnSaleFilterUsageStatisticsTest() {
    final int amount = 4;
    final FilterUsageReportDto filterUsageReportDto = getFilterUsageReportDto();
    final FilterUsageReportDTO filterUsageReportDTO = getFilterUsageReportDTO();

    when(productsOnSaleFilterWeeklyAnalyticsUseCase.getWeeklyStatistics(amount)).thenReturn(filterUsageReportDto);
    when(filterUsageReportDTOMapper.fromModel(filterUsageReportDto)).thenReturn(filterUsageReportDTO);

    mockMvc.perform(get("/v1/filter-analytics/products-on-sale/weekly")
        .param("amount", String.valueOf(amount)))
        .andExpect(status().isOk());

    verify(productsOnSaleFilterWeeklyAnalyticsUseCase).getWeeklyStatistics(amount);
    verify(filterUsageReportDTOMapper).fromModel(filterUsageReportDto);
  }

  @SneakyThrows
  @Test
  public void getWeeklyProductOnSaleFilterUsageStatisticsWhenAmountWrongTest() {
    final int amount = 0;

    mockMvc.perform(get("/v1/filter-analytics/products-on-sale/weekly")
        .param("amount", String.valueOf(amount)))
        .andExpect(status().isBadRequest());

    verify(productsOnSaleFilterWeeklyAnalyticsUseCase, never()).getWeeklyStatistics(amount);
    verify(filterUsageReportDTOMapper, never()).fromModel(any());
  }
}
