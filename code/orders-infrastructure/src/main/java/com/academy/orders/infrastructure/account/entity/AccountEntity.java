package com.academy.orders.infrastructure.account.entity;

import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.infrastructure.order.entity.OrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "accounts")
@Getter
@Setter
@EqualsAndHashCode(exclude = "orders")
@Builder
@ToString(exclude = "orders")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_generator")
  @SequenceGenerator(name = "accounts_generator", sequenceName = "ACCOUNTS_id_seq", allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private UserStatus status;

  @Setter(AccessLevel.PRIVATE)
  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Setter(AccessLevel.PRIVATE)
  @OneToMany(mappedBy = "account")
  @Builder.Default
  private List<OrderEntity> orders = new ArrayList<>();
}
