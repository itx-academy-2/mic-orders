package com.academy.orders.domain.ratelimit.usecase;

/**
 * Use case interface for extracting the client IP address from the current request context.
 *
 * <p>This abstraction allows different implementations depending on the environment or transport layer, e.g., HTTP servlet requests,
 * WebSocket sessions, or other protocols.</p>
 */
public interface ClientIpExtractorUseCase {
  /**
   * Extracts the client IP address from the current request context.
   *
   * @return the client IP address as a {@link String}, or a special value such as "UNKNOWN" if the IP cannot be determined.
   */
  String extractClientIp();
}
