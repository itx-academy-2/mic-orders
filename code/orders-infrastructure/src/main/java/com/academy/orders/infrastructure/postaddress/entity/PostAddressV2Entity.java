package com.academy.orders.infrastructure.postaddress.entity;

import com.academy.orders.domain.order.entity.enumerated.DeliveryMethod;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "post_addresses_v2")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"orders", "account"})
@ToString(exclude = {"orders", "account"})
@Entity
public class PostAddressV2Entity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "delivery_method", nullable = false)
  private DeliveryMethod deliveryMethod;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String department;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  @Column(name = "recipient_first_name", nullable = false, length = 50)
  private String recipientFirstName;

  @Column(name = "recipient_last_name", nullable = false, length = 50)
  private String recipientLastName;

  @Column(name = "recipient_phone", nullable = false, length = 13)
  private String recipientPhone;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @OneToMany(mappedBy = "postAddress", fetch = FetchType.LAZY)
  private List<OrderV2Entity> orders = new ArrayList<>();
}
