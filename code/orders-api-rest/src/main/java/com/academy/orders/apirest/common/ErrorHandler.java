package com.academy.orders.apirest.common;

import com.academy.orders.domain.account.exception.AccountRoleNotFoundException;
import com.academy.orders.domain.account.exception.ConcurrentUpdateException;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.cart.exception.QuantityExceedsAvailableException;
import com.academy.orders.domain.common.exception.AlreadyExistsException;
import com.academy.orders.domain.common.exception.NotFoundException;
import com.academy.orders.domain.common.exception.PaidException;
import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.order.exception.InvalidOrderStatusTransitionException;
import com.academy.orders.domain.order.exception.OrderFinalStateException;
import com.academy.orders_api_rest.generated.model.ErrorObjectDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static java.util.Objects.requireNonNull;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public final ErrorObjectDTO handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
    log.warn("Constraint violation ", ex);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .detail(requireNonNull(ex.getFieldError()).getDefaultMessage());

  }

  @ExceptionHandler(value = NotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorObjectDTO handleNotFoundException(final NotFoundException ex) {
    log.warn("Can't find entity", ex);
    return new ErrorObjectDTO().status(HttpStatus.NOT_FOUND.value()).title(HttpStatus.NOT_FOUND.getReasonPhrase())
        .detail(ex.getMessage());
  }

  @ExceptionHandler(AlreadyExistsException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT)
  public ErrorObjectDTO handleAccountAlreadyExistsException(final AlreadyExistsException ex) {
    log.warn("Account already exists ", ex);
    return new ErrorObjectDTO().status(HttpStatus.CONFLICT.value()).title(HttpStatus.CONFLICT.getReasonPhrase())
        .detail(ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  public ErrorObjectDTO handleUnauthorizedException(final AuthenticationException ex) {
    return new ErrorObjectDTO().status(HttpStatus.UNAUTHORIZED.value())
        .title(HttpStatus.UNAUTHORIZED.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  public ErrorObjectDTO handleAccessDeniedException(final AccessDeniedException ex) {
    return new ErrorObjectDTO().status(HttpStatus.FORBIDDEN.value()).title(HttpStatus.FORBIDDEN.getReasonPhrase())
        .detail(ex.getMessage());
  }

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorObjectDTO handleInternalError(final Throwable ex) {
    log.warn("Internal error", ex);
    return new ErrorObjectDTO().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorObjectDTO handleBadRequestException(final MethodArgumentTypeMismatchException error) {
    log.warn("Bad request, param: {}", error.getPropertyName(), error);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(error.getMessage());
  }

  @ExceptionHandler(EmptyCartException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorObjectDTO handleEmptyCartException(final EmptyCartException ex) {
    log.warn("Cart is empty.", ex);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(InsufficientProductQuantityException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT)
  public ErrorObjectDTO handleInsufficientProductQuantityException(final InsufficientProductQuantityException ex) {
    log.warn("Ordered quantity exceeds available stock for product. ", ex);
    return new ErrorObjectDTO().status(HttpStatus.CONFLICT.value()).title(HttpStatus.CONFLICT.getReasonPhrase())
        .detail(ex.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorObjectDTO handleConstraintViolationException(final ConstraintViolationException error) {
    log.warn("Bad request, param: {}", error.getMessage(), error);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(error.getMessage());
  }

  @ExceptionHandler(QuantityExceedsAvailableException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT)
  public ErrorObjectDTO handleExceedsAvailableException(final QuantityExceedsAvailableException ex) {
    log.warn("Product quantity exceeds available stock", ex);
    return new ErrorObjectDTO().status(HttpStatus.CONFLICT.value()).title(HttpStatus.CONFLICT.getReasonPhrase())
        .detail(ex.getMessage());
  }

  @ExceptionHandler(InvalidOrderStatusTransitionException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorObjectDTO handleInvalidOrderStatusTransitionException(final InvalidOrderStatusTransitionException ex) {
    log.warn("Invalid Order Status Transition", ex);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(OrderFinalStateException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorObjectDTO handleOrderFinalStateException(final OrderFinalStateException ex) {
    log.warn("The order has already been completed or canceled and cannot be updated", ex);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(PaidException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorObjectDTO handlePaidException(final PaidException ex) {
    log.warn("The order is already paid/unpaid", ex);
    return new ErrorObjectDTO().status(HttpStatus.BAD_REQUEST.value())
        .title(HttpStatus.BAD_REQUEST.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  public ErrorObjectDTO handleUsernameNotFoundException(final UsernameNotFoundException ex) {
    return new ErrorObjectDTO().status(HttpStatus.UNAUTHORIZED.value())
        .title(HttpStatus.UNAUTHORIZED.getReasonPhrase()).detail(ex.getMessage());
  }

  @ExceptionHandler(AccountRoleNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorObjectDTO handleAccountRoleNotFoundException(final AccountRoleNotFoundException ex) {
    log.warn("Account role not found", ex);
    return new ErrorObjectDTO().status(HttpStatus.NOT_FOUND.value()).title(HttpStatus.NOT_FOUND.getReasonPhrase())
        .detail(ex.getMessage());
  }

  @ExceptionHandler(ConcurrentUpdateException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT)
  public ErrorObjectDTO handleConcurrentUpdateException(final ConcurrentUpdateException ex) {
    return new ErrorObjectDTO().status(HttpStatus.CONFLICT.value()).detail(ex.getMessage());
  }
}
