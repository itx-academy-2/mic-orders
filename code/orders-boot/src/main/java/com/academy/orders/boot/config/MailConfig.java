package com.academy.orders.boot.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
  private final String userName;

  private final String password;

  private final String host;

  private final int port;

  /**
   * Constructs a MailConfig instance with the specified SMTP configuration properties.
   *
   * @param userName the SMTP username from application properties
   * @param password the SMTP password from application properties
   * @param host the SMTP server host from application properties
   * @param port the SMTP server port from application properties, defaulting to 587 if not specified
   */
  public MailConfig(@Value("${spring.mail.username}") String userName, @Value("${spring.mail.password}") String password,
      @Value("${spring.mail.host}") String host, @Value("${spring.mail.port:587}") int port) {
    this.userName = userName;
    this.password = password;
    this.host = host;
    this.port = port;
  }

  /**
   * Creates and configures a {@link JavaMailSender} bean for sending emails using SMTP.
   *
   * The mail sender is set up with the specified host, port, username, and password, and is configured to use SMTP authentication and STARTTLS encryption.
   *
   * @return a configured {@link JavaMailSender} instance
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
}
