package com.academy.orders.infrastructure.cart.repository;

import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.entity.CreateCartItemDTO;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.cart.CartItemMapper;
import com.academy.orders.infrastructure.cart.entity.CartItemEntity;
import com.academy.orders.infrastructure.cart.entity.CartItemId;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.getCartItem;
import static com.academy.orders.infrastructure.ModelUtils.getCartItemEntity;
import static com.academy.orders.infrastructure.ModelUtils.getProduct;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemRepositoryTest {
  @InjectMocks
  private CartItemRepositoryImpl cartItemRepository;

  @Mock
  private CartItemJpaAdapter cartItemJpaAdapter;

  @Mock
  private CartItemMapper cartItemMapper;

  @Mock
  private AccountJpaAdapter accountJpaAdapter;

  @Mock
  private ProductJpaAdapter productJpaAdapter;

  private CreateCartItemDTO createCartItemDto;

  private CartItemEntity cartItemEntity;

  @BeforeEach
  void setUp() {
    createCartItemDto = new CreateCartItemDTO(UUID.randomUUID(), 1L, 1);
    cartItemEntity = CartItemEntity.builder().quantity(1).build();
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void existsByProductAndUserIdTest(Boolean response) {

    when(cartItemJpaAdapter.existsById(any(CartItemId.class))).thenReturn(response);

    assertEquals(response, cartItemRepository.existsByProductIdAndUserId(UUID.randomUUID(), 1L));
    verify(cartItemJpaAdapter).existsById(any(CartItemId.class));
  }

  @Test
  void saveCartItemTest() {
    var productEntity = getProduct();
    var expected = CartItem.builder().product(productEntity).quantity(1).build();

    when(cartItemMapper.toEntity(createCartItemDto)).thenReturn(cartItemEntity);
    when(productJpaAdapter.getReferenceById(createCartItemDto.productId())).thenReturn(new ProductEntity());
    when(accountJpaAdapter.getReferenceById(createCartItemDto.userId())).thenReturn(new AccountEntity());
    when(cartItemJpaAdapter.save(any(CartItemEntity.class))).thenReturn(cartItemEntity);
    when(cartItemMapper.fromEntity(cartItemEntity)).thenReturn(expected);

    var actualOrderItem = cartItemRepository.save(createCartItemDto);

    assertEquals(expected, actualOrderItem);
    assertNotNull(cartItemEntity.getAccount());
    assertNotNull(cartItemEntity.getProduct());

    verify(cartItemMapper).toEntity(any(CreateCartItemDTO.class));
    verify(productJpaAdapter).getReferenceById(any(UUID.class));
    verify(accountJpaAdapter).getReferenceById(anyLong());
    verify(cartItemJpaAdapter).save(any(CartItemEntity.class));
    verify(cartItemMapper).fromEntity(any(CartItemEntity.class));
  }

  @Test
  void saveCartItemWithExistingTest() {
    var existingCartItemEntity = getCartItemEntity();
    var updatedQuantity = 2;

    when(cartItemJpaAdapter.findById(any(CartItemId.class))).thenReturn(Optional.of(existingCartItemEntity));
    when(cartItemJpaAdapter.save(any(CartItemEntity.class))).thenReturn(existingCartItemEntity);
    when(cartItemMapper.fromEntity(existingCartItemEntity)).thenReturn(getCartItem());

    createCartItemDto = new CreateCartItemDTO(existingCartItemEntity.getProduct().getId(),
        existingCartItemEntity.getAccount().getId(), updatedQuantity);

    cartItemRepository.save(createCartItemDto);

    assertEquals(updatedQuantity, existingCartItemEntity.getQuantity());
    verify(cartItemJpaAdapter).findById(any(CartItemId.class));
    verify(cartItemJpaAdapter).save(existingCartItemEntity);
    verify(cartItemMapper).fromEntity(existingCartItemEntity);
  }

  @Test
  void findCartItemsByAccountIdTest() {
    var cartItem = getCartItemEntity();
    var cartItemEntities = singletonList(cartItem);
    var cartItems = singletonList(getCartItem());

    when(cartItemJpaAdapter.findAllByAccountId(anyLong())).thenReturn(cartItemEntities);
    when(cartItemMapper.fromEntities(cartItemEntities)).thenReturn(cartItems);

    var actualCartItems = cartItemRepository.findCartItemsByAccountId(anyLong());

    assertEquals(cartItems, actualCartItems);

    verify(cartItemJpaAdapter).findAllByAccountId(anyLong());
    verify(cartItemMapper).fromEntities(anyList());
  }

  @Test
  void deleteCartItemsByAccountIdTest() {
    doNothing().when(cartItemJpaAdapter).deleteAllByAccountId(anyLong());

    assertDoesNotThrow(() -> cartItemRepository.deleteCartItemsByAccountId(1L));
    verify(cartItemJpaAdapter).deleteAllByAccountId(anyLong());
  }

  @Test
  void deleteCartItemByAccountAndProductIdsTest() {
    var productId = UUID.randomUUID();
    var accountId = 1L;

    doNothing().when(cartItemJpaAdapter).deleteByAccountIdAndProductId(accountId, productId);

    assertDoesNotThrow(() -> cartItemRepository.deleteCartItemByAccountAndProductIds(accountId, productId));
    verify(cartItemJpaAdapter).deleteByAccountIdAndProductId(anyLong(), any(UUID.class));
  }

  @Test
  void findCartItemsByAccountIdAndLangTest() {
    var lang = "uk";
    var accountId = 1L;
    var cartItem = getCartItemEntity();
    var cartItemEntities = singletonList(cartItem);
    var cartItems = singletonList(getCartItem());

    when(cartItemJpaAdapter.findAllByAccountIdAndProductLang(accountId, lang)).thenReturn(cartItemEntities);
    when(cartItemMapper.fromEntitiesWithProductsTranslations(cartItemEntities)).thenReturn(cartItems);
    var actualItems = cartItemRepository.findCartItemsByAccountIdAndLang(accountId, lang);

    assertEquals(cartItems, actualItems);
    verify(cartItemJpaAdapter).findAllByAccountIdAndProductLang(anyLong(), anyString());
    verify(cartItemMapper).fromEntitiesWithProductsTranslations(any());

  }

  @Test
  void findByProductIdAndUserIdTest() {
    var productId = UUID.randomUUID();
    var userId = 1L;
    var cartItem = getCartItemEntity();
    var expectedCartItem = getCartItem();

    when(cartItemJpaAdapter.findById(new CartItemId(productId, userId))).thenReturn(Optional.of(cartItem));
    when(cartItemMapper.fromEntity(cartItem)).thenReturn(expectedCartItem);

    var actualCartItem = cartItemRepository.findByProductIdAndUserId(productId, userId);

    Assertions.assertTrue(actualCartItem.isPresent());
    assertEquals(expectedCartItem, actualCartItem.get());

    verify(cartItemJpaAdapter).findById(new CartItemId(productId, userId));
    verify(cartItemMapper).fromEntity(cartItem);

  }
}
