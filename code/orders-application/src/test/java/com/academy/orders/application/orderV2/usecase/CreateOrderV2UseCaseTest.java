package com.academy.orders.application.orderV2.usecase;

import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;
import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.application.ModelUtils.getCartItem;
import static com.academy.orders.application.ModelUtils.getCreateOrderV2Dto;
import static com.academy.orders.application.ModelUtils.getPostAddressV2;
import static com.academy.orders.application.ModelUtils.getAccountV2;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class CreateOrderV2UseCaseTest {
    @Mock
    private OrderV2Repository orderV2Repository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ChangeQuantityUseCase changeQuantityUseCase;

    @Mock
    private AccountV2Repository accountV2Repository;

    @InjectMocks
    private CreateOrderV2UseCaseImpl createOrderV2UseCase;

    private CreateOrderV2Dto createOrderV2Dto;

    private CartItem cartItem;

    private BigDecimal calculatedPrice;

    @BeforeEach
    void setUp() {
        createOrderV2Dto = getCreateOrderV2Dto();
        cartItem = getCartItem();
        calculatedPrice = cartItem.product().getPrice().multiply(BigDecimal.valueOf(cartItem.quantity()));
    }

    @Test
    void createOrderV2_SuccessTest() {
        //Given
        var expectedOrderId = UUID.randomUUID();
        var order = OrderV2.builder().postAddress(getPostAddressV2(createOrderV2Dto))
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderItems(singletonList(new OrderItem(cartItem.product(), calculatedPrice, null, cartItem.quantity())))
                .account(getAccountV2())
                .isPaid(false).build();

        when(cartItemRepository.findCartItemsByAccountId(anyLong())).thenReturn(singletonList(cartItem));
        doNothing().when(changeQuantityUseCase).changeQuantityOfProduct(any(Product.class), anyInt());
        when(accountV2Repository.findAccountById(anyLong())).thenReturn(Optional.of(getAccountV2()));
        when(orderV2Repository.save(eq(order), anyLong())).thenReturn(expectedOrderId);
        doNothing().when(cartItemRepository).deleteCartItemsByAccountId(anyLong());

        //When
        var actualOrderId = createOrderV2UseCase.createOrderV2(createOrderV2Dto, 1L);

        //Then
        assertEquals(expectedOrderId, actualOrderId);

        verify(cartItemRepository).findCartItemsByAccountId(anyLong());
        verify(changeQuantityUseCase).changeQuantityOfProduct(any(Product.class), anyInt());
        verify(orderV2Repository).save(any(OrderV2.class), anyLong());
        verify(cartItemRepository).deleteCartItemsByAccountId(anyLong());
    }

    @Test
    void createOrderV2_ThrowsInsufficientProductQuantityExceptionTest() {
        //Given
        when(cartItemRepository.findCartItemsByAccountId(anyLong())).thenReturn(singletonList(cartItem));
        doThrow(InsufficientProductQuantityException.class).when(changeQuantityUseCase)
                .changeQuantityOfProduct(any(Product.class), anyInt());

        //When
        assertThrows(InsufficientProductQuantityException.class,
                () -> createOrderV2UseCase.createOrderV2(createOrderV2Dto, 1L));

        //Then
        verify(cartItemRepository).findCartItemsByAccountId(anyLong());
        verify(changeQuantityUseCase).changeQuantityOfProduct(any(Product.class), anyInt());
    }

    @Test
    void createOrderV2_ThrowsEmptyCartExceptionTest() {
        //Given
        when(cartItemRepository.findCartItemsByAccountId(anyLong())).thenReturn(emptyList());

        //When
        assertThrows(EmptyCartException.class, () -> createOrderV2UseCase.createOrderV2(createOrderV2Dto, 1L));

        //Then
        verify(cartItemRepository).findCartItemsByAccountId(anyLong());
    }
}
