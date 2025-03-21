package com.academy.orders.infrastructure.order.entity;

import com.academy.orders.infrastructure.product.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"order", "product"})
@ToString(exclude = {"order", "product"})
public class OrderItemEntity {

  @EmbeddedId
  @Builder.Default
  private OrderItemId orderItemId = new OrderItemId();

  @Column(nullable = false)
  private BigDecimal price;

  @Column
  private Integer discount;

  @Column(nullable = false)
  private Integer quantity;

  @MapsId("orderId")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @MapsId("productId")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  public void setOrder(OrderEntity order) {
    this.order = order;
    this.orderItemId.setOrderId(order.getId());
  }

  public void setProduct(ProductEntity product) {
    this.product = product;
    this.orderItemId.setProductId(product.getId());
  }
}
