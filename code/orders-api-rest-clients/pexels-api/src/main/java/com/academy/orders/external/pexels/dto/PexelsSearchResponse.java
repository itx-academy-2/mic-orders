package com.academy.orders.external.pexels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PexelsSearchResponse {
  @JsonProperty("total_results")
  private int totalResults;

  private List<PexelsPhotoItem> photos;
}
