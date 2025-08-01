package com.academy.orders.application.postaddress.usecase;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.academy.orders.application.ModelUtils.getPostAddressV2;
import static com.academy.orders.application.ModelUtils.getPostAddressV2WithNewData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetPermanentPostAddressesUseCaseTest {
    @Mock
    private PostAddressV2Repository postAddressV2Repository;

    @InjectMocks
    private GetPermanentPostAddressesUseCaseImpl useCase;

    private Long userId;
    private List<PostAddressV2> mockAddresses;

    @BeforeEach
    void setUp() {
        userId = 1L;
        mockAddresses = List.of(getPostAddressV2(), getPostAddressV2WithNewData());
    }

    @Test
    void getPermanentPostAddressesByUserId_ReturnsListFromRepository_Test() {
        // Given
        when(postAddressV2Repository.getPermanentPostAddressesByUserId(userId)).thenReturn(mockAddresses);

        // When
        List<PostAddressV2> result = useCase.getPermanentPostAddressesByUserId(userId);

        // Then
        assertEquals(mockAddresses, result);
        assertTrue(result.get(0).title().startsWith("permanent: "));
        assertTrue(result.get(1).title().startsWith("permanent: "));
        verify(postAddressV2Repository).getPermanentPostAddressesByUserId(userId);
    }

    @Test
    void getPermanentPostAddressesByUserId_CallsRepositoryWithCorrectUserId_Test() {
        // Given
        when(postAddressV2Repository.getPermanentPostAddressesByUserId(any())).thenReturn(mockAddresses);

        // When
        useCase.getPermanentPostAddressesByUserId(userId);

        // Then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(postAddressV2Repository).getPermanentPostAddressesByUserId(captor.capture());
        assertEquals(userId, captor.getValue());
    }
}
