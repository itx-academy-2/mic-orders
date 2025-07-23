package com.academy.orders.application.passwordreset;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.academy.orders.application.passwordreset.usecase.ProcessPasswordResetUseCaseImpl;
import com.academy.orders.domain.passwordreset.dto.PasswordResetEmailCommand;
import com.academy.orders.domain.passwordreset.usecase.SendPasswordResetEmailUseCase;
import com.academy.orders.domain.ratelimit.dto.RateLimitResult;
import com.academy.orders.domain.ratelimit.usecase.RequestPasswordResetUseCase;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class ProcessPasswordResetUseCaseImplTest {
    @Mock
    private RequestPasswordResetUseCase requestPasswordResetUseCase;
    @Mock
    private SendPasswordResetEmailUseCase sendPasswordResetEmailUseCase;
    @InjectMocks
    private ProcessPasswordResetUseCaseImpl processPasswordResetUseCase;

    private ValidatorFactory factory;
    private ListAppender<ILoggingEvent> logAppender;

    static final String EMAIL = "user@example.com";
    static final String CLIENT_IP = "192.168.1.1";

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        processPasswordResetUseCase = new ProcessPasswordResetUseCaseImpl(
            requestPasswordResetUseCase,
            sendPasswordResetEmailUseCase,
            validator
        );

        Logger logger = LoggerFactory.getLogger(ProcessPasswordResetUseCaseImpl.class);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        logAppender = new ListAppender<>();
        logAppender.setContext(loggerContext);
        logAppender.start();

        ((ch.qos.logback.classic.Logger) logger).detachAndStopAllAppenders();
        ((ch.qos.logback.classic.Logger) logger).addAppender(logAppender);
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    void processPasswordReset_shouldSendEmailAndLogSuccess_whenRateLimitAllows() {
        // Given
        var rateLimitResult = new RateLimitResult(true, 5, 60L, 4, 120L);
        when(requestPasswordResetUseCase.requestPasswordReset(Mockito.any(PasswordResetEmailCommand.class)))
            .thenReturn(rateLimitResult);

        // When
        processPasswordResetUseCase.processPasswordReset(EMAIL, CLIENT_IP);

        // Then
        verify(requestPasswordResetUseCase).requestPasswordReset(
           argThat(command -> command.email().equals(EMAIL) && command.ipAddress().equals(CLIENT_IP))
        );
        verify(sendPasswordResetEmailUseCase).sendResetEmail(EMAIL);

        assertThat(logAppender.list)
            .hasSize(1)
            .extracting(ILoggingEvent::getFormattedMessage)
            .containsExactly("Password reset email sent to " + EMAIL);

        assertThat(logAppender.list.get(0).getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void processPasswordReset_shouldNotSendEmailAndLogRateLimitExceeded_whenRateLimitDisallows() {
        // Given
        var rateLimitResult = new RateLimitResult(false, 0, 0L, 0, 0L);
        when(requestPasswordResetUseCase.requestPasswordReset(Mockito.any(PasswordResetEmailCommand.class)))
            .thenReturn(rateLimitResult);

        // When
        processPasswordResetUseCase.processPasswordReset(EMAIL, CLIENT_IP);

        // Then
        verify(requestPasswordResetUseCase).requestPasswordReset(
            argThat(command -> command.email().equals(EMAIL) && command.ipAddress().equals(CLIENT_IP))
        );
        verifyNoInteractions(sendPasswordResetEmailUseCase);

        assertThat(logAppender.list)
            .hasSize(1)
            .extracting(ILoggingEvent::getFormattedMessage)
            .containsExactly("Password reset rate limit exceeded for email " + EMAIL);

        assertThat(logAppender.list.get(0).getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void processPasswordReset_shouldThrowConstraintViolationException_whenEmailIsBlank() {
        // Given
        String blankEmail = "";

        // When & Then
        assertThatThrownBy(() -> processPasswordResetUseCase.processPasswordReset(blankEmail, CLIENT_IP))
            .isInstanceOf(ConstraintViolationException.class)
            .satisfies(ex -> {
                String msg = ex.getMessage();
                assertThat(msg).contains("email");
                assertThat(msg.length()).isGreaterThan(0);
            });

        Mockito.verifyNoInteractions(requestPasswordResetUseCase, sendPasswordResetEmailUseCase);
        assertThat(logAppender.list).isEmpty();
    }

    @Test
    void processPasswordReset_shouldThrowConstraintViolationException_whenIpAddressIsBlank() {
        // Given
        String blankIp = "";

        // When & Then
        assertThatThrownBy(() -> processPasswordResetUseCase.processPasswordReset(EMAIL, blankIp))
            .isInstanceOf(ConstraintViolationException.class)
            .satisfies(ex -> {
                String msg = ex.getMessage();
                assertThat(msg).contains("ipAddress");
                assertThat(msg.length()).isGreaterThan(0);
            });

        Mockito.verifyNoInteractions(requestPasswordResetUseCase, sendPasswordResetEmailUseCase);
        assertThat(logAppender.list).isEmpty();
    }

    @Test
    void processPasswordReset_shouldThrowConstraintViolationException_whenEmailIsNull() {
        // When & Then
        assertThatThrownBy(() -> processPasswordResetUseCase.processPasswordReset(null, CLIENT_IP))
            .isInstanceOf(ConstraintViolationException.class)
            .satisfies(ex -> {
                String msg = ex.getMessage();
                assertThat(msg).contains("email");
            });

        verifyNoInteractions(requestPasswordResetUseCase, sendPasswordResetEmailUseCase);
        assertThat(logAppender.list).isEmpty();
    }

    @Test
    void processPasswordReset_shouldThrowConstraintViolationException_whenIpAddressIsNull() {
        // When & Then
        assertThatThrownBy(() -> processPasswordResetUseCase.processPasswordReset(EMAIL, null))
            .isInstanceOf(ConstraintViolationException.class)
            .satisfies(ex -> {
                String msg = ex.getMessage();
                assertThat(msg).contains("ipAddress");
            });
        verifyNoInteractions(requestPasswordResetUseCase, sendPasswordResetEmailUseCase);
        assertThat(logAppender.list).isEmpty();
    }

    @Test
    void processPasswordReset_shouldLogInfoOnly_whenSuccessful() {
        // Given
        var rateLimitResult = new RateLimitResult(true, 5, 60L, 4, 120L);
        when(requestPasswordResetUseCase.requestPasswordReset(Mockito.any())).thenReturn(rateLimitResult);

        // When
        processPasswordResetUseCase.processPasswordReset(EMAIL, CLIENT_IP);

        // Then
        assertThat(logAppender.list)
            .extracting(ILoggingEvent::getLevel)
            .doesNotContain(Level.ERROR, Level.WARN);
    }

    @Test
    void processPasswordReset_shouldThrowNPE_whenRequestPasswordResetReturnsNull() {
        // Given
        when(requestPasswordResetUseCase.requestPasswordReset(Mockito.any())).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> processPasswordResetUseCase.processPasswordReset(EMAIL, CLIENT_IP))
            .isInstanceOf(NullPointerException.class);

        verify(requestPasswordResetUseCase).requestPasswordReset(Mockito.any());
        verifyNoInteractions(sendPasswordResetEmailUseCase);
    }

    @Test
    void processPasswordReset_shouldCallSendEmailWithCorrectEmail() {
        // Given
        var rateLimitResult = new RateLimitResult(true, 5, 60L, 4, 120L);
        when(requestPasswordResetUseCase.requestPasswordReset(Mockito.any())).thenReturn(rateLimitResult);

        // When
        processPasswordResetUseCase.processPasswordReset(EMAIL, CLIENT_IP);

        // Then
        verify(sendPasswordResetEmailUseCase).sendResetEmail(EMAIL);
    }

    @Test
    void processPasswordReset_shouldLogRateLimitExceededMessage() {
        // Given
        var rateLimitResult = new RateLimitResult(false, 0, 0L, 0, 0L);
        when(requestPasswordResetUseCase.requestPasswordReset(Mockito.any())).thenReturn(rateLimitResult);

        // When
        processPasswordResetUseCase.processPasswordReset(EMAIL, CLIENT_IP);

        // Then
        assertThat(logAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage)
            .anyMatch(msg -> msg.contains("rate limit exceeded") && msg.contains(EMAIL));
    }
}

