package com.academy.orders.apirest.analytics.mapper;

import com.academy.orders.domain.filter.dto.FilterUsageReportDto;
import com.academy.orders_api_rest.generated.model.FilterUsageReportDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FilterUsageReportDTOMapper {
  FilterUsageReportDTO fromModel(final FilterUsageReportDto filterUsageReportDto);
}
