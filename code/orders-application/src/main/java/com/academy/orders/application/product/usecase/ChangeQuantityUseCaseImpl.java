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
  public void changeQuantityOfProduct(Product product, Integer orderedQuantity) {
    var quantityOfProductsLeft = getQuantityOfProductsLeft(product, orderedQuantity);
    setNewQuantity(product.getId(), quantityOfProductsLeft);
  }

  private int getQuantityOfProductsLeft(Product product, Integer orderedQuantity) {
    var quantityDifference = product.getQuantity() - orderedQuantity;
    if (quantityDifference < 0 || orderedQuantity <= 0)
      throw new InsufficientProductQuantityException(product.getId());

    return quantityDifference;
  }

  private void setNewQuantity(UUID productId, Integer quantity) {
    productRepository.setNewProductQuantity(productId, quantity);
  }
}
