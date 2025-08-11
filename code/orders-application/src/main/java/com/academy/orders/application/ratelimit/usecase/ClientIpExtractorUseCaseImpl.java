package com.academy.orders.application.ratelimit.usecase;

import com.academy.orders.domain.ratelimit.usecase.ClientIpExtractorUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Implementation of {@link ClientIpExtractorUseCase} that extracts the client IP address from the current HTTP request context.
 *
 * <p>It tries to obtain the IP address from the "X-Forwarded-For" header, which is commonly used by proxies/load balancers. If this header
 * is missing or empty, falls back to the remote address of the HTTP servlet request.</p>
 *
 * <p>If the current request context is not available, returns "UNKNOWN".</p>
 */
@Slf4j
@Service
public class ClientIpExtractorUseCaseImpl implements ClientIpExtractorUseCase {
  @Override
  public String extractClientIp() {
    var requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes servletAttributes)) {
      log.warn("No ServletRequestAttributes available in current context. Returning UNKNOWN as client IP.");
      return "UNKNOWN";
    }
    var request = servletAttributes.getRequest();
    var ip = request.getHeader("X-Forwarded-For");

    if (ip != null && !ip.isBlank()) {
      String clientIp = ip.split(",")[0].trim();
      log.debug("Extracted client IP from X-Forwarded-For header: {}", clientIp);
      return clientIp;
    }

    String remoteAddr = request.getRemoteAddr();
    log.debug("X-Forwarded-For header is missing or empty. Using remote address: {}", remoteAddr);
    return remoteAddr;
  }
}
