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

  public MailConfig(@Value("${spring.mail.username}") String userName, @Value("${spring.mail.password}") String password,
      @Value("${spring.mail.host}") String host, @Value("${spring.mail.port:587}") int port) {
    this.userName = userName;
    this.password = password;
    this.host = host;
    this.port = port;
  }

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
