package com.academy.orders.domain.filter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PeriodFilterUsageDto {
  private LocalDate startDate;

  private LocalDate endDate;

  private Integer count;
}
