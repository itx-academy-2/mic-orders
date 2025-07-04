package com.academy.orders.application.orderV2.usecase;

import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.OrderReceiver;
import com.academy.orders.domain.order.entity.PostAddress;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;
import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import com.academy.orders.domain.orderV2.usecase.CreateOrderV2UseCase;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderV2UseCaseImpl implements CreateOrderV2UseCase {
    private final OrderV2Repository orderV2Repository;

    private final CartItemRepository cartItemRepository;

    private final ChangeQuantityUseCase changeQuantityUseCase;


    @Override
    public UUID createOrderV2(CreateOrderV2Dto orderV2Dto, Long accountId) {
        var bucketElements = getBucketElements(accountId);
        checkCartIsNotEmpty(bucketElements);
        var orderItems = createOrderItems(bucketElements);
        var order = createOrderObject(orderV2Dto, orderItems);
        var orderId = saveOrder(order, accountId);
        clearCart(accountId);
        return orderId;
    }

    private List<CartItem> getBucketElements(Long accountId) {
        return cartItemRepository.findCartItemsByAccountId(accountId);
    }

    private void checkCartIsNotEmpty(List<CartItem> bucketElements) {
        if (Objects.isNull(bucketElements) || bucketElements.isEmpty()) {
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

    private OrderV2 createOrderObject(CreateOrderV2Dto createOrderV2Dto, List<OrderItem> orderItems) {
        return OrderV2.builder().receiver(createReceiverObject(createOrderV2Dto))
                .postAddress(createPostAddressObject(createOrderV2Dto)).orderStatus(OrderStatus.IN_PROGRESS).isPaid(false)
                .orderItems(orderItems).build();
    }

    private OrderReceiver createReceiverObject(CreateOrderV2Dto createOrderV2Dto) {
        return OrderReceiver.builder().firstName(createOrderV2Dto.firstName()).lastName(createOrderV2Dto.lastName())
                .email(createOrderV2Dto.email()).build();
    }

    private PostAddress createPostAddressObject(CreateOrderV2Dto createOrderV2Dto) {
        return PostAddress.builder().city(createOrderV2Dto.city()).department(createOrderV2Dto.department())
                .deliveryMethod(createOrderV2Dto.deliveryMethod()).build();
    }

    private UUID saveOrder(OrderV2 orderV2, Long accountId) {
        return orderV2Repository.save(orderV2, accountId);
    }

    private void clearCart(Long accountId) {
        cartItemRepository.deleteCartItemsByAccountId(accountId);
    }
}
