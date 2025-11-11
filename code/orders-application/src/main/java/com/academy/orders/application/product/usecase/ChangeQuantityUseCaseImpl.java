package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeQuantityUseCaseImpl implements ChangeQuantityUseCase {

  private final ProductRepository productRepository;

  @Override
  public void changeQuantityOfProduct(Product product, int delta) {
    int newQuantity = product.getQuantity() - delta; // subtracting delta (so +delta reduces)

    if (newQuantity < 0) {
      throw new InsufficientProductQuantityException(product.getId());
    }

    productRepository.setNewProductQuantity(product.getId(), newQuantity);
  }
}
