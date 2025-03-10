package com.academy.orders.application.order.usecase;

import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.order.dto.CreateOrderDto;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.OrderReceiver;
import com.academy.orders.domain.order.entity.PostAddress;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.order.repository.OrderRepository;
import com.academy.orders.domain.order.usecase.CreateOrderUseCase;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
  private final ChangeQuantityUseCase changeQuantityUseCase;

  private final OrderRepository orderRepository;

  private final CartItemRepository cartItemRepository;

  @Override
  @Transactional
  public UUID createOrder(CreateOrderDto createOrderDto, Long accountId) {
    var bucketElements = getBucketElements(accountId);
    checkCartIsNotEmpty(bucketElements);
    var orderItems = createOrderItems(bucketElements);
    var order = createOrderObject(createOrderDto, orderItems);
    var orderId = saveOrder(order, accountId);
    clearCart(accountId);
    return orderId;
  }

  private Order createOrderObject(CreateOrderDto createOrderDto, List<OrderItem> orderItems) {
    return Order.builder().receiver(createReceiverObject(createOrderDto))
        .postAddress(createPostAddressObject(createOrderDto)).orderStatus(OrderStatus.IN_PROGRESS).isPaid(false)
        .orderItems(orderItems).build();
  }

  private PostAddress createPostAddressObject(CreateOrderDto createOrderDto) {
    return PostAddress.builder().city(createOrderDto.city()).department(createOrderDto.department())
        .deliveryMethod(createOrderDto.deliveryMethod()).build();
  }

  private void checkCartIsNotEmpty(List<CartItem> bucketElements) {
    if (Objects.isNull(bucketElements) || bucketElements.isEmpty()) {
      throw new EmptyCartException();
    }
  }

  private OrderReceiver createReceiverObject(CreateOrderDto createOrderDto) {
    return OrderReceiver.builder().firstName(createOrderDto.firstName()).lastName(createOrderDto.lastName())
        .email(createOrderDto.email()).build();
  }

  private List<CartItem> getBucketElements(Long accountId) {
    return cartItemRepository.findCartItemsByAccountId(accountId);
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

  private UUID saveOrder(Order order, Long accountId) {
    return orderRepository.save(order, accountId);
  }

  private void clearCart(Long accountId) {
    cartItemRepository.deleteCartItemsByAccountId(accountId);
  }
}
