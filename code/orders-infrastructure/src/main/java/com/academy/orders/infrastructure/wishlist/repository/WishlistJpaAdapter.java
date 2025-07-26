package com.academy.orders.infrastructure.wishlist.repository;

import com.academy.orders.infrastructure.wishlist.entity.WishlistEntity;
import com.academy.orders.infrastructure.wishlist.entity.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistJpaAdapter extends JpaRepository<WishlistEntity, WishlistId> {
}
