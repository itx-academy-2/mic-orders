package com.academy.orders.infrastructure.passwordreset.repository;

import com.academy.orders.domain.passwordreset.usecase.PasswordHashingPort;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordHashingAdapterTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    private PasswordHashingPort hashingAdapter;

    @BeforeEach
    void setUp() {
        hashingAdapter = new BCryptPasswordHashingAdapter(passwordEncoder);
    }

    @Test
    void hash_shouldDelegateToPasswordEncoder() {
        // Given
        var rawPassword = "mySecret123!";
        var encodedPassword = "$2a$10$abcdefghijklmnopqrstuv";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // When
        var result = hashingAdapter.hash(rawPassword);

        // Then
        assertThat(result).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode(rawPassword);
    }
}