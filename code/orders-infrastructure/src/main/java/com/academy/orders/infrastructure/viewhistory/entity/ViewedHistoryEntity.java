package com.academy.orders.infrastructure.viewhistory.entity;

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
 * Entity representing a viewed product by a user.
 */
@Entity
@Table(name = "viewed_history")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ViewedHistoryEntity {

  @EmbeddedId
  private ViewedHistoryId id;

  @Column(name = "viewed_at", nullable = false)
  private Instant viewedAt;

  public ViewedHistoryEntity(Long accountId, UUID productId) {
    this.id = new ViewedHistoryId(accountId, productId);
    this.viewedAt = Instant.now();
  }

  public void updateViewedAt() {
    this.viewedAt = Instant.now();
  }
}
