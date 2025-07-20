package com.academy.orders.boot.infrastructure.orderV2.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.repository.OrderRepository;
import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import com.academy.orders.domain.postaddress.exception.PostAddressTitleAlreadyExistsException;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.ModelUtils.getOrderV2WithoutId;
import static com.academy.orders.ModelUtils.getOrderV2WithoutIdWithNewRecipientName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OrderV2RepositoryIT extends AbstractRepositoryIT {
    @Autowired
    private OrderV2Repository orderV2Repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final Long accountId = 2L;

    @Test
    void save_Success_Test() {
        //Given
        OrderV2 orderToSave = getOrderV2WithoutId();

        //When
        UUID savedOrderId = orderV2Repository.save(orderToSave, accountId);
        assertNotNull(savedOrderId);

        //Check if OrderV2 was also duplicated to the V1 table in the DB
        Optional<Order> optionalOrder = orderRepository.findById(savedOrderId);

        OrderV2Entity persistedOrderV2Entity = entityManager.find(OrderV2Entity.class, savedOrderId);

        //Then
        assertTrue(optionalOrder.isPresent());
        Order order = optionalOrder.get();
        assertEquals(accountId, order.account().id());
        assertEquals(orderToSave.orderStatus(), order.orderStatus());
        assertEquals(orderToSave.isPaid(), order.isPaid());
        assertEquals(orderToSave.postAddress().recipientFirstName(), order.receiver().firstName());

        assertNotNull(persistedOrderV2Entity);
        assertEquals(persistedOrderV2Entity.getId(), savedOrderId);
        assertEquals(persistedOrderV2Entity.getOrderStatus(), orderToSave.orderStatus());
        assertEquals(persistedOrderV2Entity.getIsPaid(), orderToSave.isPaid());
    }

    @Test
    void save_PostAddressTitleAlreadyExists_Test() {
        //Given
        OrderV2 orderToSave = getOrderV2WithoutId();

        //When
        orderV2Repository.save(orderToSave, accountId);

        OrderV2 orderToSaveWithSameTitle = getOrderV2WithoutIdWithNewRecipientName();

        //Then
        assertThrows(PostAddressTitleAlreadyExistsException.class, () -> orderV2Repository.save(orderToSaveWithSameTitle, accountId));
    }

    @Test
    void save_PostAddressTitleAlreadyExistsButAllAddressFieldsTheSame_Test() {
        //Given
        OrderV2 orderToSave = getOrderV2WithoutId();

        //When
        UUID savedOrderIdFirst = orderV2Repository.save(orderToSave, accountId);

        //Then
        assertNotNull(savedOrderIdFirst);

        OrderV2 orderToSaveWithSameTitle = getOrderV2WithoutId();
        UUID savedOrderIdSecond = orderV2Repository.save(orderToSaveWithSameTitle, accountId);

        assertNotNull(savedOrderIdSecond);
        assertNotEquals(savedOrderIdFirst, savedOrderIdSecond);

        //Check if OrderV2 was also duplicated to the V1 table in the DB
        Optional<Order> optionalOrder = orderRepository.findById(savedOrderIdSecond);

        OrderV2Entity persistedOrderV2Entity = entityManager.find(OrderV2Entity.class, savedOrderIdSecond);

        assertTrue(optionalOrder.isPresent());
        Order order = optionalOrder.get();
        assertEquals(accountId, order.account().id());
        assertEquals(orderToSave.orderStatus(), order.orderStatus());
        assertEquals(orderToSave.isPaid(), order.isPaid());
        assertEquals(orderToSave.postAddress().recipientFirstName(), order.receiver().firstName());

        assertNotNull(persistedOrderV2Entity);
        assertEquals(persistedOrderV2Entity.getId(), savedOrderIdSecond);
        assertEquals(persistedOrderV2Entity.getOrderStatus(), orderToSave.orderStatus());
        assertEquals(persistedOrderV2Entity.getIsPaid(), orderToSave.isPaid());
    }
}
