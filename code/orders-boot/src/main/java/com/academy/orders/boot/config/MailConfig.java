package com.academy.orders.boot.config;

import jakarta.annotation.PostConstruct;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuration class for setting up the JavaMailSender bean used for sending emails. <p> This class loads SMTP configuration properties
 * from the application environment: <ul> <li>{@code spring.mail.username} – SMTP login (required)</li> <li>{@code spring.mail.password} –
 * SMTP password (required)</li> <li>{@code spring.mail.host} – SMTP host (required)</li> <li>{@code spring.mail.port} – SMTP port (default:
 * 587)</li> </ul>
 *
 * <p> The configuration is validated at startup to ensure all required parameters are present and valid. If validation fails, an
 * {@link IllegalArgumentException} is thrown to prevent silent misconfiguration. </p>
 *
 * <p> This mail configuration is typically used for sending password reset links, registration confirmations, and other transactional
 * emails. </p>
 */
@Slf4j
@Configuration
public class MailConfig {
  private final String userName;

  private final String password;

  private final String host;

  private final int port;

  /**
   * Constructs the mail configuration using values injected from application properties.
   *
   * @param userName SMTP username
   * @param password SMTP password
   * @param host SMTP server hostname
   * @param port SMTP server port (default: 587)
   */
  public MailConfig(
      @Value("${spring.mail.username}") String userName,
      @Value("${spring.mail.password}") String password,
      @Value("${spring.mail.host}") String host,
      @Value("${spring.mail.port:587}") int port) {
    this.userName = userName;
    this.password = password;
    this.host = host;
    this.port = port;
  }

  /**
   * Validates the injected mail configuration parameters. Throws an {@link IllegalArgumentException} if any required property is missing or
   * invalid.
   */
  @PostConstruct
  public void validateMailProperties() {
    if (isBlank(userName)) {
      throw new IllegalStateException("Mail username (spring.mail.username) must not be empty");
    }
    if (isBlank(password)) {
      throw new IllegalStateException("Mail password (spring.mail.password) must not be empty");
    }
    if (isBlank(host)) {
      throw new IllegalStateException("Mail host (spring.mail.host) must not be empty");
    }
    if (port <= 0 || port > 65535) {
      throw new IllegalStateException("Mail port (spring.mail.port) must be between 1 and 65535");
    }

    if (log.isInfoEnabled()) {
      log.info("Mail configuration validated: host={}, port={}, user={}", host, port, maskEmail(userName));
    }
  }

  /**
   * Creates and configures the {@link JavaMailSender} bean.
   *
   * @return a fully configured {@link JavaMailSender} instance
   */
  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(userName);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    return mailSender;
  }

  /**
   * Checks whether the provided string is null, empty, or contains only whitespace.
   *
   * @param str the string to check
   * @return true if the string is blank, false otherwise
   */
  private boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Masks the provided email address for safe logging. Only the first character and domain are shown, e.g., j***@domain.com.
   *
   * @param email the email address to mask
   * @return a masked version of the email
   */
  private String maskEmail(String email) {
    if (isBlank(email))
      return "****";
    int atIndex = email.indexOf('@');
    if (atIndex <= 1)
      return "***";
    return email.charAt(0) + "***" + email.substring(atIndex);
  }
}
