package com.academy.orders.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SystemMetricsAutoConfiguration.class, TomcatMetricsAutoConfiguration.class})
@ComponentScan(basePackages = "com.academy.orders")
@ConfigurationPropertiesScan(basePackages = "com.academy.orders")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
