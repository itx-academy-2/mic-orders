package com.academy.orders.application.account.usecase;

import com.academy.orders.domain.account.usecase.PasswordResetEmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetEmailSenderImpl implements PasswordResetEmailSender {
  private final JavaMailSender mailSender;

  @Value("${app.frontend.reset-password-url}")
  private String frontendResetUrl;

  @Value("${spring.mail.username}")
  private String from;

  @Override
  public void send(String to, String token) {
    String subject = "Reset your password";
    String resetLink = frontendResetUrl + "/" + token;

    String text = String.format("""
        Hello,

        You requested to reset your password. Click the link below to reset it:

        %s

        If you did not request this, please ignore this email.

        Best regards,
        Your Support Team
        """, resetLink);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    mailSender.send(message);
  }
}
