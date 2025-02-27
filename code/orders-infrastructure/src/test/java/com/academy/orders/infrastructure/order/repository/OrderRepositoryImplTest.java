package com.academy.orders.infrastructure.order.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.order.OrderMapper;
import com.academy.orders.infrastructure.order.OrderPageMapper;
import com.academy.orders.infrastructure.order.entity.OrderEntity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.academy.orders.infrastructure.ModelUtils.getAccountEntity;
import static com.academy.orders.infrastructure.ModelUtils.getOrder;
import static com.academy.orders.infrastructure.ModelUtils.getOrderEntity;
import static com.academy.orders.infrastructure.ModelUtils.getOrderItemEntity;
import static com.academy.orders.infrastructure.ModelUtils.getOrdersFilterParametersDto;
import static com.academy.orders.infrastructure.ModelUtils.getPageImplOf;
import static com.academy.orders.infrastructure.ModelUtils.getPageOf;
import static com.academy.orders.infrastructure.ModelUtils.getPageable;
import static com.academy.orders.infrastructure.ModelUtils.getPostAddressEntity;
import static com.academy.orders.infrastructure.ModelUtils.getProductEntity;
import static com.academy.orders.infrastructure.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {
  @InjectMocks
  private OrderRepositoryImpl orderRepository;

  @Mock
  private OrderJpaAdapter jpaAdapter;

  @Mock
  private ProductJpaAdapter productJpaAdapter;

  @Mock
  private AccountJpaAdapter accountJpaAdapter;

  @Mock
  private OrderMapper mapper;

  @Mock
  private PageableMapper pageableMapper;

  @Mock
  private OrderPageMapper pageMapper;

  @Mock
  private CustomOrderRepository customOrderRepository;

  @Test
  void saveTest() {
    var orderEntity = createOrderEntityWithDependencies();
    var postAddressEntity = orderEntity.getPostAddress();
    var accountEntity = orderEntity.getAccount();
    var orderItemEntity = orderEntity.getOrderItems().get(0);
    var referencedProductEntity = ProductEntity.builder().id(orderItemEntity.getProduct().getId()).build();

    when(mapper.toEntity(any(Order.class))).thenReturn(orderEntity);
    when(accountJpaAdapter.getReferenceById(anyLong())).thenReturn(accountEntity);
    when(productJpaAdapter.getReferenceById(any(UUID.class))).thenReturn(referencedProductEntity);
    when(jpaAdapter.save(any())).thenReturn(orderEntity);

    var orderId = orderRepository.save(getOrder(), 1L);

    assertEquals(orderEntity.getId(), orderId);
    assertEquals(postAddressEntity.getOrder(), orderEntity);
    assertEquals(orderEntity.getAccount(), accountEntity);
    assertEquals(orderItemEntity.getOrder(), orderEntity);
    assertEquals(orderItemEntity.getProduct(), referencedProductEntity);

    verify(mapper).toEntity(any(Order.class));
    verify(accountJpaAdapter).getReferenceById(anyLong());
    verify(productJpaAdapter).getReferenceById(any(UUID.class));
    verify(jpaAdapter).save(any());
  }

  @Test
  void findByIdTest() {
    // Given
    OrderEntity order = getOrderEntity();
    UUID orderId = order.getId();
    Optional<OrderEntity> optionalOrderEntity = Optional.of(order);
    Order orderDomain = getOrder();
    Optional<Order> optionalOrder = Optional.of(orderDomain);

    when(jpaAdapter.findById(orderId)).thenReturn(optionalOrderEntity);
    when(mapper.fromEntity(order)).thenReturn(orderDomain);

    // When
    Optional<Order> result = orderRepository.findById(orderId);

    // Then
    assertEquals(optionalOrder, result);
    verify(jpaAdapter).findById(orderId);
    verify(mapper).fromEntity(order);
  }

  @Test
  void findAllTest() {
    // Given
    Pageable pageable = getPageable();
    var orderDomainPage = getPageOf(getOrder());
    var springPageable = PageRequest.of(pageable.page(), pageable.size());
    var orderEntityPage = getPageImplOf(getOrderEntity());
    var filterParametersDto = getOrdersFilterParametersDto();

    when(pageableMapper.fromDomain(pageable)).thenReturn(springPageable);
    when(customOrderRepository.findAllByFilterParameters(filterParametersDto, springPageable))
        .thenReturn(orderEntityPage);
    when(pageMapper.toDomain(orderEntityPage)).thenReturn(orderDomainPage);

    // When
    Page<Order> actual = orderRepository.findAll(filterParametersDto, pageable);

    // Then
    assertEquals(orderDomainPage, actual);
    verify(pageableMapper).fromDomain(pageable);
    verify(customOrderRepository).findAllByFilterParameters(filterParametersDto, springPageable);
    verify(pageMapper).toDomain(orderEntityPage);
  }

  @Test
  void findAllByUserIdTest() {
    // Given
    Long userId = 1L;
    String language = "uk";
    Pageable pageable = getPageable();
    var orderDomainPage = getPageOf(getOrder());
    var springPageable = PageRequest.of(pageable.page(), pageable.size());
    var orderEntityPage = getPageImplOf(getOrderEntity());

    when(pageableMapper.fromDomain(pageable)).thenReturn(springPageable);
    when(jpaAdapter.findAllByAccountId(userId, springPageable)).thenReturn(orderEntityPage);
    when(jpaAdapter.findAllOrdersByOrderIdsFetchProductData(
        orderEntityPage.getContent().stream().map(OrderEntity::getId).toList(), language))
            .thenReturn(orderEntityPage.getContent());
    when(pageMapper.toDomain(orderEntityPage)).thenReturn(orderDomainPage);

    // When
    Page<Order> actual = orderRepository.findAllByUserId(userId, language, pageable);

    // Then
    assertEquals(orderDomainPage, actual);
    verify(pageableMapper).fromDomain(pageable);
    verify(jpaAdapter).findAllByAccountId(userId, springPageable);
    verify(jpaAdapter).findAllOrdersByOrderIdsFetchProductData(
        orderEntityPage.getContent().stream().map(OrderEntity::getId).toList(), language);
    verify(pageMapper).toDomain(orderEntityPage);
  }

  @Test
  void updateOrderStatusTest() {
    UUID orderId = TEST_UUID;
    OrderStatus status = OrderStatus.COMPLETED;

    doNothing().when(jpaAdapter).updateOrderStatus(orderId, status);

    orderRepository.updateOrderStatus(orderId, status);
    verify(jpaAdapter).updateOrderStatus(orderId, status);
  }

  @Test
  void updateIsPaidStatusTest() {
    UUID orderId = TEST_UUID;

    doNothing().when(jpaAdapter).updateIsPaidStatus(orderId, true);

    orderRepository.updateIsPaidStatus(orderId, true);
    verify(jpaAdapter).updateIsPaidStatus(orderId, true);
  }

  @Test
  void findByIdFetchOrderItemsDataTest() {
    // Given
    String language = "uk";
    OrderEntity order = getOrderEntity();
    UUID orderId = order.getId();
    Optional<OrderEntity> optionalOrderEntity = Optional.of(order);
    Order orderDomain = getOrder();
    Optional<Order> optionalOrder = Optional.of(orderDomain);
    var productIds = Stream.of(order).flatMap(orderEntity -> orderEntity.getOrderItems().stream())
        .map(orderItemEntity -> orderItemEntity.getProduct().getId()).toList();
    var products = List.of(getProductEntity());

    when(jpaAdapter.findByIdFetchOrderItemsData(orderId)).thenReturn(optionalOrderEntity);
    when(productJpaAdapter.findAllByIdAndLanguageCode(productIds, language)).thenReturn(products);
    when(mapper.fromEntity(order)).thenReturn(orderDomain);

    // When
    Optional<Order> result = orderRepository.findById(orderId, language);

    // Then
    assertEquals(optionalOrder, result);
    verify(jpaAdapter).findByIdFetchOrderItemsData(orderId);
    verify(productJpaAdapter).findAllByIdAndLanguageCode(productIds, language);
    verify(mapper).fromEntity(order);
  }

  @Test
  void findByIdFetchOrderItemsDataWhenOrderNotFoundTest() {
    // Given
    String language = "uk";
    UUID orderId = TEST_UUID;
    Optional<OrderEntity> optionalOrderEntity = Optional.empty();
    Optional<Order> optionalOrder = Optional.empty();

    when(jpaAdapter.findByIdFetchOrderItemsData(orderId)).thenReturn(optionalOrderEntity);

    // When
    Optional<Order> result = orderRepository.findById(orderId, language);

    // Then
    assertEquals(optionalOrder, result);
    verify(jpaAdapter).findByIdFetchOrderItemsData(orderId);
  }

  @Test
  void findByIdFetchDataTest() {
    // Given
    OrderEntity order = getOrderEntity();
    UUID orderId = order.getId();
    Optional<OrderEntity> optionalOrderEntity = Optional.of(order);
    Order orderDomain = getOrder();
    Optional<Order> optionalOrder = Optional.of(orderDomain);

    when(jpaAdapter.findByIdFetchData(orderId)).thenReturn(optionalOrderEntity);
    when(mapper.fromEntity(order)).thenReturn(orderDomain);

    // When
    Optional<Order> result = orderRepository.findByIdFetchData(orderId);

    // Then
    assertEquals(optionalOrder, result);
    verify(jpaAdapter).findByIdFetchData(orderId);
    verify(mapper).fromEntity(order);
  }

  private OrderEntity createOrderEntityWithDependencies() {
    var orderEntity = getOrderEntity();
    var orderItemEntity = getOrderItemEntity();
    orderItemEntity.setProduct(getProductEntity());
    orderEntity.getOrderItems().add(orderItemEntity);

    var postAddressEntity = getPostAddressEntity();
    orderEntity.setPostAddress(postAddressEntity);
    postAddressEntity.setOrder(orderEntity);

    var accountEntity = getAccountEntity();
    orderEntity.setAccount(accountEntity);
    return orderEntity;
  }
}
