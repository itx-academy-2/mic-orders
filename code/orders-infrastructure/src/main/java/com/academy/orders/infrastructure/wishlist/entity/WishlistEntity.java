package com.academy.orders.infrastructure.wishlist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a product in the user's wishlist.
 */
@Entity
@Table(name = "wishlist")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class WishlistEntity {

  @EmbeddedId
  private WishlistId id;

  @Column(name = "added_at", nullable = false, updatable = false)
  private Instant addedAt;

  public WishlistEntity(Long accountId, UUID productId) {
    this.id = new WishlistId(accountId, productId);
    this.addedAt = Instant.now();
  }
}
