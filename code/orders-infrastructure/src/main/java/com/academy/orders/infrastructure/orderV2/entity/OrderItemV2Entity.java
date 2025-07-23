package com.academy.orders.infrastructure.orderV2.entity;

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

@Table(name = "order_items_v2")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"orderV2", "product"})
@ToString(exclude = {"product", "orderV2"})
public class OrderItemV2Entity {

  @EmbeddedId
  @Builder.Default
  private OrderItemV2Id orderItemV2Id = new OrderItemV2Id();

  @Column(nullable = false)
  private BigDecimal price;

  @Column
  private Integer discount;

  @Column(nullable = false)
  private Integer quantity;

  @MapsId("orderV2Id")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_v2_id", nullable = false)
  private OrderV2Entity orderV2;

  @MapsId("productId")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  public void setOrder(OrderV2Entity order) {
    this.orderV2 = order;
    this.orderItemV2Id.setOrderV2Id(order.getId());
  }

  public void setProduct(ProductEntity product) {
    this.product = product;
    this.orderItemV2Id.setProductId(product.getId());
  }
}
