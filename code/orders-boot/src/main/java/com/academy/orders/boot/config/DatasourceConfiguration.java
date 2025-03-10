package com.academy.orders.boot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * The App configuration.
 */
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = "com.academy.orders.infrastructure.**.repository",
    entityManagerFactoryRef = "retailEntityManagerFactory", transactionManagerRef = "retailTransactionManager")
public class DatasourceConfiguration {

  @Primary
  @Bean(name = "retailEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean retailEntityManagerFactory(final EntityManagerFactoryBuilder builder,
      final DataSource datasource) {

    return builder.dataSource(datasource).packages("com.academy.orders.infrastructure").build();
  }

  @Bean(name = "retailTransactionManager")
  public PlatformTransactionManager retailTransactionManager(
      @Qualifier("retailEntityManagerFactory") final LocalContainerEntityManagerFactoryBean entityManagerFactory) {

    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
  }

}
