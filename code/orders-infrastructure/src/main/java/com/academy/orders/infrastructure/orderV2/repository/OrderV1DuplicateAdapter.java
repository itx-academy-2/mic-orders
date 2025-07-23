package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.infrastructure.orderV2.entity.OrderItemV2Entity;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 * Adapter class responsible for duplicating newly created OrderV2 data into the legacy OrderV1 table structure. It directly uses native SQL
 * via {@link jakarta.persistence.EntityManager} to preserve the UUIDs and prevent Hibernate's {@code @GeneratedValue} strategy from
 * overriding them. This duplication supports a temporary coexistence phase between the new V2 architecture and the old V1 schema. It will
 * be safely removed once the migration to V2 is fully completed.
 *
 * @author Oleksandra Bulhakova
 */
@Repository
public class OrderV1DuplicateAdapter {
  @PersistenceContext
  private EntityManager entityManager;

  public void duplicateOrderV2ToV1(OrderV2Entity orderV2Entity) {
    entityManager.flush();
    var postAddress = orderV2Entity.getPostAddress();

    entityManager.createNativeQuery("""
            INSERT INTO orders (
                id, is_paid, order_status, created_at, edited_at,
                account_id, first_name, last_name, email
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)
        .setParameter(1, orderV2Entity.getId())
        .setParameter(2, orderV2Entity.getIsPaid())
        .setParameter(3, orderV2Entity.getOrderStatus().name())
        .setParameter(4, orderV2Entity.getCreatedAt())
        .setParameter(5, orderV2Entity.getEditedAt())
        .setParameter(6, orderV2Entity.getAccount().getId())
        .setParameter(7, postAddress.getRecipientFirstName())
        .setParameter(8, postAddress.getRecipientLastName())
        .setParameter(9, orderV2Entity.getAccount().getEmail())
        .executeUpdate();

    entityManager.createNativeQuery("""
            INSERT INTO post_addresses (id, delivery_method, city, department)
            VALUES (?, ?, ?, ?)
        """)
        .setParameter(1, orderV2Entity.getId())
        .setParameter(2, postAddress.getDeliveryMethod().name())
        .setParameter(3, postAddress.getCity())
        .setParameter(4, postAddress.getDepartment())
        .executeUpdate();

    for (OrderItemV2Entity item : orderV2Entity.getOrderItems()) {
      entityManager.createNativeQuery("""
              INSERT INTO order_items (order_id, product_id, price, discount, quantity)
              VALUES (?, ?, ?, ?, ?)
          """)
          .setParameter(1, orderV2Entity.getId())
          .setParameter(2, item.getProduct().getId())
          .setParameter(3, item.getPrice())
          .setParameter(4, item.getDiscount())
          .setParameter(5, item.getQuantity())
          .executeUpdate();
    }
  }
}
