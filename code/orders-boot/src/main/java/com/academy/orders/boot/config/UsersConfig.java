package com.academy.orders.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("auth")
public record UsersConfig(List<AppUser> users) {

  public record AppUser(String username, String password, String roles) {

  }

}
