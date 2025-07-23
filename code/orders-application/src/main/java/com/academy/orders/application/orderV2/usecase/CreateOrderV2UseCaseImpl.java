package com.academy.orders.application.orderV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;
import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import com.academy.orders.domain.orderV2.usecase.CreateOrderV2UseCase;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderV2UseCaseImpl implements CreateOrderV2UseCase {
  private final OrderV2Repository orderV2Repository;

  private final CartItemRepository cartItemRepository;

  private final ChangeQuantityUseCase changeQuantityUseCase;

  private final AccountV2Repository accountV2Repository;

  @Override
  public UUID createOrderV2(CreateOrderV2Dto orderV2Dto, Long accountId) {
    log.info("Creating orderV2 for accountId={}", accountId);
    var bucketElements = getBucketElements(accountId);
    checkCartIsNotEmpty(bucketElements);
    var orderItems = createOrderItems(bucketElements);
    var orderV2 = createOrderV2Object(orderV2Dto, orderItems, accountId);
    var orderV2Id = saveOrderV2(orderV2, accountId);
    log.info("OrderV2 created: orderId={}, accountId={}", orderV2Id, accountId);
    clearCart(accountId);
    return orderV2Id;
  }

  private List<CartItem> getBucketElements(Long accountId) {
    return cartItemRepository.findCartItemsByAccountId(accountId);
  }

  private void checkCartIsNotEmpty(List<CartItem> bucketElements) {
    if (Objects.isNull(bucketElements) || bucketElements.isEmpty()) {
      log.error("The cart is empty");
      throw new EmptyCartException();
    }
  }

  private List<OrderItem> createOrderItems(List<CartItem> cartItems) {
    return cartItems.stream().map(this::createItem).toList();
  }

  private OrderItem createItem(CartItem cartItem) {
    final BigDecimal calculatedPrice = CartItem.calculateCartItemPrice(cartItem);
    changeQuantityUseCase.changeQuantityOfProduct(cartItem.product(), cartItem.quantity());
    final Integer currentDiscount = cartItem.product().getDiscountAmount();
    return new OrderItem(cartItem.product(), calculatedPrice, currentDiscount, cartItem.quantity());
  }

  private OrderV2 createOrderV2Object(CreateOrderV2Dto createOrderV2Dto, List<OrderItem> orderItems,
      Long accountId) {
    return OrderV2.builder()
        .postAddress(createPostAddressV2Object(createOrderV2Dto, accountId)).orderStatus(OrderStatus.IN_PROGRESS).isPaid(false)
        .orderItems(orderItems).account(createAccountV2Object(accountId)).build();
  }

  private PostAddressV2 createPostAddressV2Object(CreateOrderV2Dto createOrderV2Dto, Long accountId) {
    return PostAddressV2.builder().city(createOrderV2Dto.city()).department(createOrderV2Dto.department())
        .deliveryMethod(createOrderV2Dto.deliveryMethod())
        .recipientFirstName(createOrderV2Dto.firstName()).recipientLastName(createOrderV2Dto.lastName())
        .recipientPhone(createOrderV2Dto.phone()).title(createOrderV2Dto.title())
        .account(createAccountV2Object(accountId))
        .id(createOrderV2Dto.addressId() != null ? UUID.fromString(createOrderV2Dto.addressId()) : null).build();
  }

  private AccountV2 createAccountV2Object(Long accountId) {
    return accountV2Repository.findAccountById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
  }

  private UUID saveOrderV2(OrderV2 orderV2, Long accountId) {
    return orderV2Repository.save(orderV2, accountId);
  }

  private void clearCart(Long accountId) {
    cartItemRepository.deleteCartItemsByAccountId(accountId);
  }
}
