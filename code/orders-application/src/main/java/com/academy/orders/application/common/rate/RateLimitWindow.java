package com.academy.orders.application.common.rate;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateLimitWindow {
  private final List<Long> timeStamps = new ArrayList<>();

  private final int maxRequests;

  private final long windowSizeInMillis;

  private final Clock clock;

  public synchronized boolean tryAddAttempt() {
    cleanExpiredAttempts();
    if (timeStamps.size() >= maxRequests) {
      return false;
    }
    timeStamps.add(clock.millis());
    return true;
  }

  public synchronized int getRemainingAttempts() {
    cleanExpiredAttempts();
    return Math.max(0, maxRequests - timeStamps.size());
  }

  public synchronized long getResetTime() {
    cleanExpiredAttempts();
    if (timeStamps.isEmpty()) {
      return clock.millis();
    }
    return timeStamps.get(0) + windowSizeInMillis;
  }

  private void cleanExpiredAttempts() {
    long now = clock.millis();
    timeStamps.removeIf(timeStamp -> timeStamp < now - windowSizeInMillis);
  }
}
