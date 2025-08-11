package com.academy.orders.application.account.usecase;

import com.academy.orders.domain.account.usecase.PasswordResetEmailSenderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetEmailSenderImpl implements PasswordResetEmailSenderUseCase {
  private final JavaMailSender mailSender;

  @Value("${app.frontend.reset-password-url}")
  private String frontendResetUrl;

  @Value("${spring.mail.username}")
  private String from;

  @Override
  public void send(String to, String token) {
    try {
      var encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
      var message = buildResetEmail(to, encodedToken);
      mailSender.send(message);
      log.info("Password reset email successfully sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send password reset email to {}", to, e);
    }
  }

  private SimpleMailMessage buildResetEmail(String to, String encodedToken) {
    var resetLink = frontendResetUrl + "/" + encodedToken;
    var subject = "Reset your password";
    var text = String.format("""
        Hello,

        You requested to reset your password. Click the link below to reset it:

        %s

        If you did not request this, please ignore this email.

        Best regards,
        Your Support Team
        """, resetLink);

    var message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    return message;
  }
}
