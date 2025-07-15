package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderV2RepositoryTest {
    @Mock
    private OrderV2JpaAdapter jpaAdapter;

    @Mock

    @InjectMocks
    private OrderV2Repository repository;


}
