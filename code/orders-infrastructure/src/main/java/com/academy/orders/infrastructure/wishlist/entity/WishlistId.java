package com.academy.orders.infrastructure.wishlist.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

/**
 * Embedded ID for wishlist table (account_id + product_id).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class WishlistId implements Serializable {

  private Long accountId;

  private UUID productId;
}
