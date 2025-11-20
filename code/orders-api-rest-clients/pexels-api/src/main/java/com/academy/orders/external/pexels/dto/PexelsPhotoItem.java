package com.academy.orders.external.pexels.dto;

import lombok.Data;

@Data
public class PexelsPhotoItem {
  private Src src;

  @Data
  public static class Src {
    private String original;
  }
}
