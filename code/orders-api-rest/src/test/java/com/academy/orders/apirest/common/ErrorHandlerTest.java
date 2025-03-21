package com.academy.orders.apirest.common;

import com.academy.orders.domain.account.exception.AccountAlreadyExistsException;
import com.academy.orders.domain.account.exception.AccountRoleNotFoundException;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.cart.exception.QuantityExceedsAvailableException;
import com.academy.orders.domain.common.exception.NotFoundException;
import com.academy.orders.domain.common.exception.PaidException;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.order.exception.InvalidOrderStatusTransitionException;
import com.academy.orders.domain.order.exception.OrderFinalStateException;
import com.academy.orders_api_rest.generated.model.ErrorObjectDTO;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
  private static final String DEFAULT_ERROR_MESSAGE = "Something went wrong";

  @InjectMocks
  private ErrorHandler errorHandler;

  @Test
  void handleMethodArgumentNotValidExceptionTest() {
    var ex = mock(MethodArgumentNotValidException.class);
    var fieldError = mock(FieldError.class);

    when(fieldError.getDefaultMessage()).thenReturn(DEFAULT_ERROR_MESSAGE);
    when(ex.getFieldError()).thenReturn(fieldError);

    assertEquals(buildErrorObjectDTO(BAD_REQUEST), errorHandler.handleMethodArgumentNotValidException(ex));
  }

  @Test
  void handleNotFoundExceptionTest() {
    var ex = mock(NotFoundException.class);

    when(ex.getMessage()).thenReturn(DEFAULT_ERROR_MESSAGE);
    assertEquals(buildErrorObjectDTO(NOT_FOUND), errorHandler.handleNotFoundException(ex));
  }

  @Test
  void handleAccountAlreadyExistsExceptionTest() {
    var email = "test@mail.com";
    var message = String.format("Account with email %s already exists", email);
    var ex = new AccountAlreadyExistsException(email);

    assertEquals(buildErrorObjectDTO(CONFLICT, message), errorHandler.handleAccountAlreadyExistsException(ex));
  }

  @Test
  void handleAuthenticationExceptionTest() {
    var ex = mock(AuthenticationException.class);
    when(ex.getMessage()).thenReturn(DEFAULT_ERROR_MESSAGE);

    assertEquals(buildErrorObjectDTO(UNAUTHORIZED), errorHandler.handleUnauthorizedException(ex));
  }

  @Test
  void handleAccessDeniedExceptionTest() {
    var ex = new AccessDeniedException(DEFAULT_ERROR_MESSAGE);
    assertEquals(buildErrorObjectDTO(FORBIDDEN), errorHandler.handleAccessDeniedException(ex));
  }

  @Test
  void handleInternalErrorTest() {
    var ex = new Throwable(DEFAULT_ERROR_MESSAGE);
    assertEquals(buildErrorObjectDTO(INTERNAL_SERVER_ERROR), errorHandler.handleInternalError(ex));
  }

  @Test
  void handleBadRequestExceptionTest() {
    var ex = mock(MethodArgumentTypeMismatchException.class);

    when(ex.getMessage()).thenReturn(DEFAULT_ERROR_MESSAGE);
    assertEquals(buildErrorObjectDTO(BAD_REQUEST), errorHandler.handleBadRequestException(ex));
  }

  @Test
  void handleInsufficientProductQuantityExceptionTest() {
    var productId = UUID.randomUUID();
    var ex = new InsufficientProductQuantityException(productId);
    var message = "Ordered quantity exceeds available stock for product: " + productId;

    assertEquals(buildErrorObjectDTO(CONFLICT, message),
        errorHandler.handleInsufficientProductQuantityException(ex));
  }

  @Test
  void handleEmptyCartExceptionTest() {
    var ex = new EmptyCartException();
    var message = "Cannot place an order with an empty cart.";

    assertEquals(buildErrorObjectDTO(BAD_REQUEST, message), errorHandler.handleEmptyCartException(ex));
  }

  @Test
  void handleConstraintViolationExceptionTest() {
    var ex = mock(ConstraintViolationException.class);

    when(ex.getMessage()).thenReturn(DEFAULT_ERROR_MESSAGE);
    assertEquals(buildErrorObjectDTO(BAD_REQUEST), errorHandler.handleConstraintViolationException(ex));
  }

  @Test
  void handleExceedsAvailableExceptionTest() {
    var productId = UUID.randomUUID();
    var quantity = 10;
    var availableQuantity = 5;
    var ex = new QuantityExceedsAvailableException(productId, quantity, availableQuantity);
    var message = "Product with id: "
        + productId + " exceeded available quantity. Requested: " + quantity
        + ", Available: " + availableQuantity;

    assertEquals(buildErrorObjectDTO(CONFLICT, message), errorHandler.handleExceedsAvailableException(ex));
  }

  @Test
  void handleInvalidOrderStatusTransitionExceptionTest() {
    var currentStatus = OrderStatus.COMPLETED;
    var newStatus = OrderStatus.SHIPPED;
    var ex = new InvalidOrderStatusTransitionException(currentStatus, newStatus);
    var expectedMessage = "Invalid status transition from " + currentStatus + " to " + newStatus;

    assertEquals(buildErrorObjectDTO(BAD_REQUEST, expectedMessage),
        errorHandler.handleInvalidOrderStatusTransitionException(ex));
  }

  @Test
  void handleOrderFinalStateExceptionTest() {
    var errorMessage = "The order has already been completed or canceled and cannot be updated.";
    var ex = new OrderFinalStateException();

    var expectedErrorObjectDTO = new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(errorMessage);

    assertEquals(expectedErrorObjectDTO, errorHandler.handleOrderFinalStateException(ex));
  }

  @Test
  void handlePaidExceptionTest() {
    var ex = mock(PaidException.class);

    when(ex.getMessage()).thenReturn(DEFAULT_ERROR_MESSAGE);
    assertEquals(buildErrorObjectDTO(BAD_REQUEST), errorHandler.handlePaidException(ex));
  }

  @Test
  void handleUsernameNotFoundExceptionTest() {
    var ex = new UsernameNotFoundException(DEFAULT_ERROR_MESSAGE);

    assertEquals(buildErrorObjectDTO(UNAUTHORIZED), errorHandler.handleUsernameNotFoundException(ex));
  }

  @Test
  void handleAccountRoleNotFoundExceptionTest() {
    var accountEmail = "test@mail.com";
    var ex = new AccountRoleNotFoundException(accountEmail);
    var message = String.format("The Role of account with email: %s is not found", accountEmail);

    assertEquals(buildErrorObjectDTO(NOT_FOUND, message), errorHandler.handleAccountRoleNotFoundException(ex));
  }

  private ErrorObjectDTO buildErrorObjectDTO(HttpStatus status, String detail) {
    return new ErrorObjectDTO(status.value(), status.getReasonPhrase(), detail);
  }

  private ErrorObjectDTO buildErrorObjectDTO(HttpStatus status) {
    return new ErrorObjectDTO(status.value(), status.getReasonPhrase(), DEFAULT_ERROR_MESSAGE);
  }
}
