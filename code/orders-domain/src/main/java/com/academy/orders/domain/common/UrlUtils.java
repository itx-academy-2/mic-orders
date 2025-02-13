package com.academy.orders.domain.common;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlUtils {
  public static boolean isValidUri(final String url) {
    if (url == null)
      return false;
    try {
      new URL(url).toURI();
      return true;
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }
  }
}
