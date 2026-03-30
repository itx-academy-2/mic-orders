package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeQuantityUseCaseImpl implements ChangeQuantityUseCase {

  private final ProductRepository productRepository;

  @Override
  @Transactional
  public void changeQuantityOfProduct(Product product, int delta) {
    int newQuantity = product.getQuantity() - delta;

    if (newQuantity < 0) {
      throw new InsufficientProductQuantityException(product.getId());
    }

    productRepository.setNewProductQuantity(product.getId(), newQuantity, product.getVersion());
  }
}
